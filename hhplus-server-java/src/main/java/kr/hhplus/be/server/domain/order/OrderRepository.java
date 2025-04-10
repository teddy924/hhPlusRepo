package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.*;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Long> saveAndReturnId(Order order);

    Optional<Order> findById(Long id);

    Optional<OrderAddress> findAddressById(Long id);

    List<OrderItem> findItemByOrderId(Long id);

    Optional<OrderCoupon> findCouponById(Long id);

    Optional<OrderHistory> findHistoryById(Long id);

    void saveOrder(Order order);

    void saveOrderAddress(OrderAddress orderAddress);

    void saveOrderItems(List<OrderItem> orderItems);

    void saveOrderCoupon(OrderCoupon orderCoupon);

    void saveOrderHistory(OrderHistory orderHistory);
}
