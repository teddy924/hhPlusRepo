package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaOrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    Optional<OrderHistory> findByOrderId(Long id);

}
