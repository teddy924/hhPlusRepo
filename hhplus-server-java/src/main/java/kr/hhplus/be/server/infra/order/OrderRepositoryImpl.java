package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Optional<Long> saveAndReturnId(Order order) {
        return Optional.empty();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<OrderAddress> findAddressById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<OrderItem> findItemByOrderId(Long id) {
        return List.of();
    }

    @Override
    public Optional<OrderCoupon> findCouponById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<OrderHistory> findHistoryById(Long id) {
        return Optional.empty();
    }

    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public void saveOrderAddress(OrderAddress orderAddress) {

    }

    @Override
    public void saveOrderItems(List<OrderItem> orderItems) {

    }

    @Override
    public void saveOrderCoupon(OrderCoupon orderCoupon) {

    }

    @Override
    public void saveOrderHistory(OrderHistory orderHistory) {

    }
}
