package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.product.ProductLockService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.application.account.AccountService;
import kr.hhplus.be.server.domain.coupon.CouponApplyInfo;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.application.payment.PaymentService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.interfaces.order.OrderDetailResponseDTO;
import kr.hhplus.be.server.interfaces.payment.PaymentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final ProductService productService;
    private final ProductLockService productLockService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final AccountService accountService;
    private final PaymentService paymentService;

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    // 주문 + 결제
    @Transactional      // 	상태 변경 많고 실패 시 전체 롤백 필요
    public OrderResult order(OrderCommand command) throws Exception {

        // 1. 상품 처리 일괄 수행
        Map<Product, Integer> orderProductMap = productService.processOrderProducts(command.productGrp());
        Long totalPrice = orderService.calculateTotalAmount(orderProductMap);

        User user = userRepository.getById(command.userId());

        // 2. 재고 차감
       command.productGrp().forEach(productLockService::decreaseStockLock);

        // 3. 쿠폰 적용
        CouponInfo couponInfo = null;
        CouponApplyInfo couponApplyInfo = CouponApplyInfo.builder().discountAmount(0L).finalPayableAmount(totalPrice).build();

        if (command.couponId() != null) {
            // 쿠폰, 발급이력 조회
            couponInfo = couponService.retrieveCouponInfo(new CouponIssueCommand(command.userId(), command.couponId()));

            // 할인 금액 계산
            couponApplyInfo = couponInfo.applyDiscount(totalPrice);
        }

        // 4. 주문 저장
        OrderInfo.OrderInfoBuilder orderInfoBuilder = OrderInfo.builder()
                .userId(user.getId())
                .productGrp(command.productGrp())
                .totPrice(totalPrice);

        OrderInfo orderInfo = orderInfoBuilder.build();

        Order order = orderService.saveInitialOrder(user, orderInfo);

        // 5. 결제 처리 (잔액 차감 + 이력 저장 + 결제 이력 저장)
        processPayment(command.userId(), couponApplyInfo.finalPayableAmount(), order);

        // 6. 주문 상세 정보 저장
        OrderCoupon orderCoupon = null;
        if (couponInfo != null && couponInfo.couponIssue().getCoupon().getId() != null) {
            orderCoupon = orderService.buildOrderCoupon(couponInfo, order, couponApplyInfo.discountAmount());
        }

        OrderSaveInfo.OrderSaveInfoBuilder builder = OrderSaveInfo.builder()
                .order(order)
                .orderAddress(orderService.buildOrderAddress(order, command.orderAddressInfo()))
                .orderItems(orderService.buildOrderItemList(order, orderProductMap))
                .orderHistory(orderService.buildOrderHistory(order, OrderHistoryStatus.PAID));

        if (orderCoupon != null) {
            builder.orderCoupon(orderCoupon);
        }

        OrderSaveInfo orderSaveInfo = builder.build();
        orderService.saveOrderRelated(orderSaveInfo);

        // 7. 쿠폰 사용 처리
        if (couponInfo != null) {
            couponService.useCoupon(couponInfo);
        }

//        // 7. 상품 수량 차감
//        orderSaveInfo.orderItems().forEach(item ->
//                productService.decreaseStock(item.getProduct().getId(), item.getQuantity())
//        );

        return OrderResult.builder().orderId(order.getId()).build();
    }

    // 주문 취소
    @Transactional      // 상태 변경 다건 + 예외 시 전체 롤백 필요
    public void cancel(OrderCancelCommand command) throws Exception {

        // 1. 주문 이력 조회
        OrderSaveInfo orderSaveInfo = orderService.retrieveOrderInfo(command.orderId());

        // 2. 상품 수량 복구
        orderSaveInfo.orderItems().forEach(item -> {productLockService.restoreStockLock(item.getProduct().getId(), item.getQuantity());});

        // 3. 주문 상태 변경 - 취소
        Order order = orderService.buildOrder(orderSaveInfo.order(), OrderStatus.CANCELED);

        // 4. 주문 취소 이력 추가
        OrderHistory cancelHistory = orderService.buildOrderHistory(order, OrderHistoryStatus.CANCELED);

        // 5. 주문 변경사항 저장
        orderService.saveOrderRelated(
                OrderSaveInfo.builder()
                        .order(order)
                        .orderHistory(cancelHistory)
                        .build()
        );

        // 6. 잔액 복구
        accountService.chargeAmount(
                AccountInfo.builder()
                        .userId(order.getUser().getId())
                        .amount(order.getTotalAmount()) // 또는 orderPayment.getAmount()
                        .build()
        );

        // 7. 잔액 이력 저장
        accountService.saveHist(
                AccountInfo.builder()
                        .userId(order.getUser().getId())
                        .amount(order.getTotalAmount())
                        .build(),
                AccountHistType.REFUND
        );

        // 8. 결제 상태 변경
        paymentService.save(
                order,
                PaymentInfo.builder()
                        .orderId(order.getId())
                        .amount(order.getTotalAmount())
                        .method(PaymentMethod.BALANCE)
                        .status(PaymentStatus.CANCELLED)
                        .build()
        );


        // 9. 쿠폰 복구
        if (orderSaveInfo.orderCoupon() != null) {
            couponService.restoreCoupon(order.getUser().getId(), orderSaveInfo.orderCoupon().getCouponIssueId());
        }

    }

    // 결제처리
    private void processPayment(Long userId, Long amount, Order order) throws Exception {
        AccountInfo accountInfo = AccountInfo.builder().userId(userId).amount(amount).build();

        accountService.useAmount(accountInfo);
        accountService.saveHist(accountInfo, AccountHistType.USE);

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .amount(amount)
                .method(PaymentMethod.BALANCE)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        paymentService.save(order, paymentInfo);
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)     // LAZY 로딩 발생 가능, readOnly 붙이면 성능 개선 및 예외 방지
    public OrderDetailResponseDTO retrieveOrderDetail(Long orderId) {
        Order order = orderRepository.getById(orderId);

        List<OrderItemDTO> itemDTOs = orderService.retrieveOrderItemInfo(orderId);
        OrderAddressDTO addressDTO = orderService.retrieveOrderAddressInfo(orderId);

        Payment payment = paymentService.retrieve(orderId)
                .orElseThrow(() ->new CustomException(NOT_EXIST_PAYMENT));
        PaymentDTO paymentDto = PaymentDTO.from(payment);


        return OrderDetailResponseDTO.from(order, itemDTOs, addressDTO, paymentDto, order.getOrderStatus());
    }

}
