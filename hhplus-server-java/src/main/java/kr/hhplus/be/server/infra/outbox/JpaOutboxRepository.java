package kr.hhplus.be.server.infra.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, Long> {

    Optional<OutboxEvent> findById(Long outboxEventId);

    List<OutboxEvent> findByStatus(OutboxStatus status);
}
