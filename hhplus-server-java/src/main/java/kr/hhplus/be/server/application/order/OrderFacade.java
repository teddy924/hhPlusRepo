package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.account.AccountCommand;
import kr.hhplus.be.server.application.account.AccountService;
import kr.hhplus.be.server.application.coupon.CouponInfo;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.coupon.CouponValidCommand;
import kr.hhplus.be.server.application.payment.PaymentInfo;
import kr.hhplus.be.server.application.payment.PaymentService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.order.OrderHistoryStatus;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final AccountService accountService;
    private final PaymentService paymentService;


    // 주문 + 결제
    public void order(OrderCommand command) throws Exception {

        PaymentInfo paymentInfo = null;
        OrderInfo orderInfo = null;
        OrderSaveInfo orderSaveInfo = null;
        AccountCommand accountCommand = null;
        CouponInfo couponInfo = null;

        Order order = null;
        OrderAddress address = null;
        List<OrderItem> orderItemList = null;
        OrderCoupon orderCoupon = null;
        OrderHistory orderHistory = null;

        Long totProductPrice = 0L;
        Long useCpnPrice = 0L;

        // 1. 상품 확인 (유효성 체크 + 상품 총액 확인)
        // 상품 ID 기준으로 상품 정보 추출
        Map<Product, Long> orderProductMap = command.productGrp().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> productService.retrieveProduct(entry.getKey()),
                        Map.Entry::getValue
                ));
        // 상품 별 유효성 체크
        orderProductMap.keySet().forEach(Product::validSalesAvailability);
        // 상품 총액 계산
        totProductPrice = orderService.calculateTotalAmount(orderProductMap);

        // 2. 쿠폰 확인 (보유 여부 + 유효성)
        if (command.couponId() != null) {
            // 쿠폰 유효성 체크
            CouponValidCommand couponValidCommand = new CouponValidCommand(command.userId(), command.couponId());
            couponInfo = couponService.validCoupon(couponValidCommand);
            // 쿠폰 적용 상품 총액 계산
            useCpnPrice = Math.max(0, totProductPrice - couponInfo.coupon().calculateCoupon(totProductPrice));


        }

        // 3. 잔액 확인
        accountCommand = AccountCommand.builder().userId(command.userId()).amount(useCpnPrice).build();

        // 3. 주문 저장
        orderInfo = OrderInfo.builder()
                .userId(command.userId())
                .productGrp(command.productGrp())
                .couponId(command.couponId())
                .totPrice(useCpnPrice).build();

        order = orderService.saveInitialOrder(orderInfo);


        // 4. 결제 처리 (잔액 차감 + 이력 저장)
        // 잔액차감 + 이력 저장
        accountService.useAmount(accountCommand);
        accountService.saveHist(accountCommand, AccountHistType.USE);

        // 결제 이력 저장
        paymentInfo = PaymentInfo.builder()
                .orderId(order.getId())
                .amount(useCpnPrice)
                .method(PaymentMethod.BALANCE)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        paymentService.payment(paymentInfo);

        // 5. 주문 상태 갱신
        order = orderService.saveOrder(order, OrderStatus.PAID);
        address = orderService.setOrderAddress(order, command.orderAddressInfo());
        orderItemList = orderService.setOrderItemList(order, orderProductMap);
        if (couponInfo != null && couponInfo.couponIssue().getCouponId() != null) {
            orderCoupon = orderService.setOrderCoupon(couponInfo, order, totProductPrice);
        }
        orderHistory = orderService.setOrderHistory(order, OrderHistoryStatus.PAID);

        orderSaveInfo = OrderSaveInfo.builder()
                .order(order)
                .orderAddress(address)
                .orderItems(orderItemList)
                .orderCoupon(orderCoupon)
                .orderHistory(orderHistory)
                .build();

        orderService.saveOrderRelated(orderSaveInfo);

        // 6. 쿠폰 사용 이력 저장
        if (couponInfo != null && couponInfo.couponIssue().getCouponId() != null) {
            couponService.useCoupon(couponInfo);
        }

        // 7. 상품 수량 차감

    }

    // 주문 취소
    public void cancel(OrderCancelCommand command) throws Exception {

        OrderSaveInfo orderSaveInfo = null;

        // 1. 주문 이력 조회
        orderSaveInfo = orderService.retrieveOrderInfo(command.orderId());

        // . 주문 상태 변경 - 취소



        // . 주문 취소 이력 추가


        // . 결제 취소/환불


        // . 쿠폰 복구


        // . 상품 수량 복구

    }
}
