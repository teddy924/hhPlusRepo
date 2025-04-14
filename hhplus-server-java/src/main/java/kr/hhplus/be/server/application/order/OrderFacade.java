package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.account.AccountInfo;
import kr.hhplus.be.server.application.account.AccountService;
import kr.hhplus.be.server.application.coupon.CouponInfo;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.coupon.CouponValidCommand;
import kr.hhplus.be.server.application.payment.PaymentInfo;
import kr.hhplus.be.server.application.payment.PaymentService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.interfaces.order.OrderAddressDTO;
import kr.hhplus.be.server.interfaces.order.OrderDetailResponseDTO;
import kr.hhplus.be.server.interfaces.order.OrderItemDTO;
import kr.hhplus.be.server.interfaces.payment.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.config.swagger.ErrorCode.NOT_EXIST_ORDER;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final AccountService accountService;
    private final PaymentService paymentService;

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 + 결제
    public void order(OrderCommand command) throws Exception {

        // 1. 상품 처리 일괄 수행
        Map<Product, Long> orderProductMap = productService.processOrderProducts(command.productGrp());
        Long totalPrice = orderService.calculateTotalAmount(orderProductMap);

        // 2. 쿠폰 적용
        CouponInfo couponInfo = null;
        Long discountAmount = 0L;
        Long finalPayableAmount = totalPrice;

        if (command.couponId() != null) {
            // 쿠폰 유효성 검증
            couponInfo = couponService.validCoupon(
                    new CouponValidCommand(command.userId(), command.couponId())
            );

            // 할인 금액 계산
            discountAmount = couponInfo.coupon().calculateCoupon(totalPrice);
            finalPayableAmount = Math.max(0L, totalPrice - discountAmount);
        }

        // 3. 주문 저장
        Order order = orderService.saveInitialOrder(
                OrderInfo.builder()
                        .userId(command.userId())
                        .productGrp(command.productGrp())
                        .couponId(command.couponId())
                        .totPrice(finalPayableAmount)
                        .build()
        );

        // 4. 결제 처리 (잔액 차감 + 이력 저장 + 결제 이력 저장)
        processPayment(command.userId(), finalPayableAmount, order.getId());

        // 5. 주문 상세 정보 저장
        OrderCoupon orderCoupon = null;
        if (couponInfo != null && couponInfo.couponIssue().getCouponId() != null) {
            orderCoupon = orderService.buildOrderCoupon(couponInfo, order, discountAmount);
        }

        OrderSaveInfo orderSaveInfo = OrderSaveInfo.builder()
                .order(order)
                .orderAddress(orderService.buildOrderAddress(order, command.orderAddressInfo()))
                .orderItems(orderService.buildOrderItemList(order, orderProductMap))
                .orderCoupon(orderCoupon)
                .orderHistory(orderService.buildOrderHistory(order, OrderHistoryStatus.PAID))
                .build();

        orderService.saveOrderRelated(orderSaveInfo);

        // 6. 쿠폰 사용 처리
        if (couponInfo != null) {
            couponService.useCoupon(couponInfo);
        }

        // 7. 상품 수량 차감
        orderSaveInfo.orderItems().forEach(item ->
                productService.decreaseStock(item.getProductId(), item.getQuantity())
        );
    }

    // 주문 취소
    public void cancel(OrderCancelCommand command) throws Exception {

        OrderSaveInfo orderSaveInfo = null;
        Order order = null;
        OrderHistory cancelHistory = null;

        // 1. 주문 이력 조회
        orderSaveInfo = orderService.retrieveOrderInfo(command.orderId());

        // 2. 주문 상태 변경 - 취소
        order = orderService.buildOrder(orderSaveInfo.order(), OrderStatus.CANCELED);
        // 3. 주문 취소 이력 추가
        cancelHistory = orderService.buildOrderHistory(order, OrderHistoryStatus.CANCELED);
        // 4. 주문 변경사항 저장
        orderService.saveOrderRelated(
                OrderSaveInfo.builder()
                        .order(order)
                        .orderHistory(cancelHistory)
                        .build()
        );

        // 5. 잔액 복구
        accountService.chargeAmount(
                AccountInfo.builder()
                        .userId(order.getUserId())
                        .amount(order.getTotalAmount()) // 또는 orderPayment.getAmount()
                        .build()
        );

        // 6. 잔액 이력 저장
        accountService.saveHist(
                AccountInfo.builder()
                        .userId(order.getUserId())
                        .amount(order.getTotalAmount())
                        .build(),
                AccountHistType.REFUND
        );

        // 7. 결제 상태 변경
        paymentService.save(
                PaymentInfo.builder()
                        .orderId(command.orderId())
                        .status(PaymentStatus.CANCELLED)
                        .build()
        );


        // 8. 쿠폰 복구
        if (orderSaveInfo.orderCoupon() != null) {
            couponService.restoreCouponUsage(order.getUserId(), orderSaveInfo.orderCoupon().getCouponIssueId());
        }

        // 9. 상품 수량 복구
        orderSaveInfo.orderItems().forEach(item -> productService.restoreStock(item.getProductId(), item.getQuantity()));
    }

    // 결제처리
    private void processPayment(Long userId, Long amount, Long orderId) throws Exception {
        AccountInfo accountInfo = AccountInfo.builder().userId(userId).amount(amount).build();

        accountService.useAmount(accountInfo);
        accountService.saveHist(accountInfo, AccountHistType.USE);

        paymentService.save(PaymentInfo.builder()
                .orderId(orderId)
                .amount(amount)
                .method(PaymentMethod.BALANCE)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build()
        );
    }

    // 주문 상세 조회
    public OrderDetailResponseDTO retrieveOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_ORDER));

        List<OrderItemDTO> items = orderItemRepository.findAllByOrderId(orderId);
        OrderAddressDTO address = orderAddressRepository.findByOrderId(orderId);

        Payment payment = paymentService.retrieveByOrderId(orderId);
        PaymentDTO paymentDto = PaymentDTO.from(payment);


        return OrderDetailResponseDTO.from(order, items, address, paymentDto, order.getOrderStatus());
    }
}
