package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {

    Long saveAndReturnId(Order order);

    Order getById(Long id);

    void save(Order order);

    List<Order> getAllByUserId(Long userId);

    List<Order> getBySysCretDtBetween(LocalDateTime start, LocalDateTime end);

}
