package kr.hhplus.be.server.infra.outbox;

import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final JpaOutboxRepository jpaOutboxRepository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return jpaOutboxRepository.save(outboxEvent);
    }

    @Override
    public OutboxEvent findById(Long outboxEventId) {
        return jpaOutboxRepository.findById(outboxEventId).orElseThrow(() -> new CustomException(NOT_EXIST_OUTBOX_EVENT));
    }

    @Override
    @Query(value = """
        SELECT oe.*
        FROM outbox_event oe
        JOIN (
            SELECT aggregate_id, event_type, MAX(created_at) AS latest_time
            FROM outbox_event
            WHERE status IN (:statuses)
              AND created_at > :timeCursor
            GROUP BY aggregate_id, event_type
        ) latest
        ON oe.aggregate_id = latest.aggregate_id
        AND oe.event_type = latest.event_type
        AND oe.created_at = latest.latest_time
        ORDER BY oe.created_at ASC
        LIMIT 100
    """, nativeQuery = true)
    public List<OutboxEvent> findLatestDistinctEvents(List<String> statuses, LocalDateTime timeCursor) {
        return List.of();
    }

    @Override
    public List<OutboxEvent> findByStatus(OutboxStatus status) {
        return jpaOutboxRepository.findByStatus(status);
    }
}
