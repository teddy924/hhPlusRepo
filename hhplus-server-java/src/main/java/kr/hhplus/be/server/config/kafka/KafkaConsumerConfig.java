package kr.hhplus.be.server.config.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.infra.outbox.OutboxEvent;
import kr.hhplus.be.server.infra.outbox.OutboxRepository;
import kr.hhplus.be.server.infra.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static final String ORDER_DLQ_TOPIC = "order.created.v1.dlq";
    private static final String COUPON_DLQ_TOPIC = "coupon.FCIssued.v1.dlq";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepository outboxRepository;

    // String consumer
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        return factory;
    }

    // OrderExternalCommand consumer
    @Bean
    public ConsumerFactory<String, String> orderCommandConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order.group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // DLQ 설정 포함
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> orderCommandKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCommandConsumerFactory());

        // DLQ + Outbox insert-only 실패 처리 커스텀 recoverer
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(ORDER_DLQ_TOPIC, record.partition())
        ) {
            @Override
            public void accept(ConsumerRecord<?, ?> record, Exception ex) {
                super.accept(record, ex);
                try {
                    String value = record.value().toString();
                    JsonNode node = new ObjectMapper().readTree(value);
                    Long outboxEventId = node.get("outboxEventId").asLong();

                    OutboxEvent original = outboxRepository.findById(outboxEventId);

                    OutboxEvent failedEvent = OutboxEvent.builder()
                            .aggregateType(original.getAggregateType())
                            .aggregateId(original.getAggregateId())
                            .eventType(original.getEventType())
                            .payload(original.getPayload())
                            .status(OutboxStatus.FAILED_CONSUMER)
                            .build();

                    outboxRepository.save(failedEvent);
                    log.info("accept : {}", failedEvent.getId());

                } catch (Exception e) {
                    log.error("DLQ 처리 중 예외 발생", e);
                }
            }
        };

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(3000L, 2));
        errorHandler.setCommitRecovered(true); // DLQ 전송 후 커밋

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    // CouponIssuedCommand consumer
    @Bean
    public ConsumerFactory<String, String> couponIssueCommandConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "coupon.group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // DLQ 설정 포함
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> couponIssueCommandKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        System.out.println("✅ factory 생성됨: couponIssueCommandKafkaListenerContainerFactory");
        log.info("✅ factory 생성됨: couponIssueCommandKafkaListenerContainerFactory");
        factory.setConsumerFactory(couponIssueCommandConsumerFactory());

        // DLQ + Outbox insert-only 실패 처리 커스텀 recoverer
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(COUPON_DLQ_TOPIC, record.partition())
        ) {
            @Override
            public void accept(ConsumerRecord<?, ?> record, Exception ex) {
                super.accept(record, ex);
                try {
                    String value = record.value().toString();
                    JsonNode node = new ObjectMapper().readTree(value);
                    Long outboxEventId = node.get("outboxEventId").asLong();

                    OutboxEvent original = outboxRepository.findById(outboxEventId);

                    OutboxEvent failedEvent = OutboxEvent.builder()
                            .aggregateType(original.getAggregateType())
                            .aggregateId(original.getAggregateId())
                            .eventType(original.getEventType())
                            .payload(original.getPayload())
                            .status(OutboxStatus.FAILED_CONSUMER)
                            .build();

                    outboxRepository.save(failedEvent);
                    log.info("accept : {}", failedEvent.getId());

                } catch (Exception e) {
                    log.error("DLQ 처리 중 예외 발생", e);
                }
            }
        };

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(3000L, 2));
        errorHandler.setCommitRecovered(true); // DLQ 전송 후 커밋

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

}
