package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

    @NonNull
    Optional<Order> findById(@NonNull Long id);

    List<Order> findAllByUserId(Long userId);

    List<Order> findAllBySysCretDtBetween(LocalDateTime start, LocalDateTime end);

}
