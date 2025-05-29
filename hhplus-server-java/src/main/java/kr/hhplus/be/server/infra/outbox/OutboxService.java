package kr.hhplus.be.server.infra.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository OutboxRepository;

    public OutboxEvent saveOutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return OutboxRepository.save(event); // 저장 후 ID 포함된 entity 반환
    }
}
