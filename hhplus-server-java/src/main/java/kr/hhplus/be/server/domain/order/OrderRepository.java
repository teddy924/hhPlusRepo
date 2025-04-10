package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.*;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Long> saveAndReturnId(Order order);

    Optional<Order> findById(Long id);

    void saveOrder(Order order);

    List<Order> findAllByUserId(Long userId);

}
