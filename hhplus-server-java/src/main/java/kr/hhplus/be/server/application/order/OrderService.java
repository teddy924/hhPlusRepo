package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.interfaces.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    public Order saveInitialOrder(User user, OrderInfo orderInfo) {
        Order order = Order.builder()
                .user(user)
                .totalAmount(orderInfo.totPrice())
                .orderStatus(OrderStatus.CREATED)
                .build();

        Long orderId = orderRepository.saveAndReturnId(order);
        return orderRepository.getById(orderId);
    }

    public Order buildOrder(Order order, OrderStatus orderStatus) {

        return Order.builder()
                .id(order.getId())
                .user(order.getUser())
                .totalAmount(order.getTotalAmount())
                .orderStatus(orderStatus)
                .sysCretDt(order.getSysCretDt())
                .sysChgDt(LocalDateTime.now())
                .build();
    }

    public OrderAddress buildOrderAddress(Order order, OrderAddressInfo orderAddressInfo) {

        return OrderAddress.builder()
                .order(order)
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
                            .order(order)
                            .product(product)
                            .quantity(quantity.intValue())
                            .totalAmount(product.getPrice() * quantity)
                            .build();
                })
                .toList());

        orderItemList.sort(Comparator.comparing(OrderItem::getQuantity));

        return orderItemList;
    }

    public OrderCoupon buildOrderCoupon(CouponInfo couponInfo, Order order, Long totProductPrice) {

        return OrderCoupon.builder()
                .order(order)
                .couponIssueId(couponInfo.couponIssue().getId())
                .discountAmount(couponInfo.coupon().calculateCoupon(totProductPrice))
                .usedDt(LocalDateTime.now())
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public OrderHistory buildOrderHistory(Order order, OrderHistoryStatus status)  {

        return OrderHistory.builder()
                .order(order)
                .status(status)
                .sysCretDt(LocalDateTime.now())
                .build();

    }

    public void saveOrderRelated(OrderSaveInfo orderSaveInfo) {
        if (orderSaveInfo.order() != null) orderRepository.save(orderSaveInfo.order());
        if (orderSaveInfo.orderAddress() != null) orderAddressRepository.save(orderSaveInfo.orderAddress());
        if (orderSaveInfo.orderItems() != null) orderItemRepository.save(orderSaveInfo.orderItems());
        if (orderSaveInfo.orderCoupon() != null) orderCouponRepository.save(orderSaveInfo.orderCoupon());
        if (orderSaveInfo.orderHistory() != null) orderHistoryRepository.save(orderSaveInfo.orderHistory());
    }

    public OrderSaveInfo retrieveOrderInfo(Long orderId) {

        return OrderSaveInfo.builder()
                .order(orderRepository.getById(orderId))
                .orderAddress(orderAddressRepository.getByOrderId(orderId))
                .orderItems(orderItemRepository.getByOrderId(orderId))
                .orderCoupon(orderCouponRepository.getByOrderId(orderId))
                .orderHistory(orderHistoryRepository.getByOrderId(orderId))
                .build();

    }

    public List<OrderResponseDTO> retrieveOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.getAllByUserId(userId);
        return orders.stream()
                .map(OrderResponseDTO::from)
                .toList();
    }

}
