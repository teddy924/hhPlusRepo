package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.coupon.CouponInfo;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.interfaces.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderCouponRepository orderCouponRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderItemRepository orderItemRepository;

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

    public Order buildOrder(Order order, OrderStatus orderStatus) {

        return Order.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(orderStatus)
                .sysCretDt(order.getSysCretDt())
                .sysChgDt(LocalDateTime.now())
                .build();
    }

    public OrderAddress buildOrderAddress(Order order, OrderAddressInfo orderAddressInfo) {

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

    public List<OrderItem> buildOrderItemList(Order order, Map<Product, Long> orderProductMap) {

        List<OrderItem> orderItemList = new ArrayList<>(
                orderProductMap.entrySet().stream()
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
                .toList());

        orderItemList.sort(Comparator.comparing(OrderItem::getQuantity));

        return orderItemList;
    }

    public OrderCoupon buildOrderCoupon(CouponInfo couponInfo, Order order, Long totProductPrice) {

        return OrderCoupon.builder()
                .orderId(order.getId())
                .couponIssueId(couponInfo.couponIssue().getId())
                .discountAmount(couponInfo.coupon().calculateCoupon(totProductPrice))
                .usedDt(LocalDateTime.now())
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public OrderHistory buildOrderHistory(Order order, OrderHistoryStatus status)  {

        return OrderHistory.builder()
                .orderId(order.getId())
                .status(status)
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public void saveOrderRelated(OrderSaveInfo orderSaveInfo) {
        orderRepository.saveOrder(orderSaveInfo.order());
        orderAddressRepository.saveOrderAddress(orderSaveInfo.orderAddress());
        orderItemRepository.saveOrderItems(orderSaveInfo.orderItems());
        orderCouponRepository.saveOrderCoupon(orderSaveInfo.orderCoupon());
        orderHistoryRepository.saveOrderHistory(orderSaveInfo.orderHistory());
    }

    public OrderSaveInfo retrieveOrderInfo(Long orderId) {

        return OrderSaveInfo.builder()
                .order(orderRepository.findById(orderId).orElseThrow(RuntimeException::new))
                .orderAddress(orderAddressRepository.findAddressById(orderId))
                .orderItems(orderItemRepository.findItemByOrderId(orderId))
                .orderCoupon(orderCouponRepository.findCouponById(orderId).orElseThrow(RuntimeException::new))
                .orderHistory(orderHistoryRepository.findHistoryById(orderId).orElseThrow(RuntimeException::new))
                .build();

    }

    public List<OrderResponseDTO> retrieveOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        return orders.stream()
                .map(OrderResponseDTO::from)
                .toList();
    }



}
