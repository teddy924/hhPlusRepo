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
    public void saveOrder(Order order) {

    }

    @Override
    public List<Order> findAllByUserId(Long userId) {
        return List.of();
    }
}
