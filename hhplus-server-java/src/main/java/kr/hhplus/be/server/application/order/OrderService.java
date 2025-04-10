package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.coupon.CouponInfo;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderHistoryStatus;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Long calculateTotalAmount(Map<Product, Long> orderProductMap) {
        return orderProductMap.entrySet().stream()
                .mapToLong(entry -> (entry.getKey().getPrice() * entry.getValue()))
                .sum();
    }

    public Order saveInitialOrder(OrderInfo orderInfo) {
        Order order = Order.builder()
                .userId(orderInfo.userId())
                .totalAmount(orderInfo.totPrice())
                .orderStatus(OrderStatus.CREATED)
                .build();

        Long orderId = orderRepository.saveAndReturnId(order).orElseThrow(RuntimeException::new);

        return orderRepository.findById(orderId).orElseThrow(() -> new CustomException(NOT_EXIST_ORDER));
    }

    public Order saveOrder(Order order, OrderStatus orderStatus) {

        return Order.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(orderStatus)
                .sysCretDt(order.getSysCretDt())
                .sysChgDt(LocalDateTime.now())
                .build();
    }

    public OrderAddress setOrderAddress(Order order, OrderAddressInfo orderAddressInfo) {

        return OrderAddress.builder()
                .orderId(order.getId())
                .receiverName(orderAddressInfo.receiverName())
                .phone(orderAddressInfo.phone())
                .address1(orderAddressInfo.address1())
                .address2(orderAddressInfo.address2())
                .zipcode(orderAddressInfo.zipcode())
                .memo(orderAddressInfo.memo())
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public List<OrderItem> setOrderItemList(Order order, Map<Product, Long> orderProductMap) {

        return orderProductMap.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    Long quantity = entry.getValue();

                    return OrderItem.builder()
                            .orderId(order.getId())
                            .productId(product.getId())
                            .quantity(quantity.intValue())
                            .totAmount(product.getPrice() * quantity)
                            .build();
                })
                .toList();
    }

    public OrderCoupon setOrderCoupon(CouponInfo couponInfo, Order order, Long totProductPrice) {

        return OrderCoupon.builder()
                .orderId(order.getId())
                .couponIssueId(couponInfo.couponIssue().getId())
                .discountAmount(couponInfo.coupon().calculateCoupon(totProductPrice))
                .usedDt(LocalDateTime.now())
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public OrderHistory setOrderHistory(Order order, OrderHistoryStatus status)  {

        return OrderHistory.builder()
                .orderId(order.getId())
                .status(status)
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public void saveOrderRelated(OrderSaveInfo orderSaveInfo) {
        orderRepository.saveOrder(orderSaveInfo.order());
        orderRepository.saveOrderAddress(orderSaveInfo.orderAddress());
        orderRepository.saveOrderItems(orderSaveInfo.orderItems());
        orderRepository.saveOrderCoupon(orderSaveInfo.orderCoupon());
        orderRepository.saveOrderHistory(orderSaveInfo.orderHistory());
    }

    public OrderSaveInfo retrieveOrderInfo(Long orderId) {

        return OrderSaveInfo.builder()
                .order(orderRepository.findById(orderId).orElseThrow(RuntimeException::new))
                .orderAddress(orderRepository.findAddressById(orderId).orElseThrow(RuntimeException::new))
                .orderItems(orderRepository.findItemByOrderId(orderId))
                .orderCoupon(orderRepository.findCouponById(orderId).orElseThrow(RuntimeException::new))
                .orderHistory(orderRepository.findHistoryById(orderId).orElseThrow(RuntimeException::new))
                .build();

    }

}
