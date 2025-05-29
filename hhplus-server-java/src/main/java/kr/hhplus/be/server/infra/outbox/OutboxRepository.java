package kr.hhplus.be.server.infra.outbox;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository {

    OutboxEvent save(OutboxEvent outboxEvent);

    OutboxEvent findById(Long outboxEventId);

    List<OutboxEvent> findLatestDistinctEvents(@Param("statuses") List<String> statuses, @Param("timeCursor") LocalDateTime timeCursor);

    List<OutboxEvent> findByStatus(OutboxStatus status);

}
