package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Long saveAndReturnId(Order order) {
        Order result = jpaOrderRepository.save(order);
        return result.getId();
    }

    @Override
    public Order getById(Long id) {
        return jpaOrderRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_EXIST_ORDER));
    }

    @Override
    public void save(Order order) {
        jpaOrderRepository.save(order);
    }

    @Override
    public List<Order> getAllByUserId(Long userId) {
        return jpaOrderRepository.findAllByUserId(userId);
    }

    @Override
    public List<Order> getBySysCretDtBetween(LocalDateTime start, LocalDateTime end) {
        return jpaOrderRepository.findAllBySysCretDtBetween(start, end);
    }
}
