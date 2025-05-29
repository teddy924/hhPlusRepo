package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.config.KafkaConsumerTestConfig;
import kr.hhplus.be.server.config.KafkaProducerTestConfig;
import kr.hhplus.be.server.config.RedissonTestConfig;
import kr.hhplus.be.server.infra.external.dataPlatform.ExternalClient;
import kr.hhplus.be.server.infra.outbox.OutboxEvent;
import kr.hhplus.be.server.infra.outbox.OutboxRepository;
import kr.hhplus.be.server.infra.outbox.OutboxStatus;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({RedissonTestConfig.class, KafkaProducerTestConfig.class, KafkaConsumerTestConfig.class})
public class OrderKafkaIntegrationTest {


    private static final Logger log = LoggerFactory.getLogger(OrderKafkaIntegrationTest.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private OutboxRepository outboxRepository;

    private static final String TOPIC = "order.created.v1";

    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicReference<String> consumedMessage = new AtomicReference<>();

    private ConcurrentMessageListenerContainer<String, String> container;

    @MockBean
    private ExternalClient externalClient;

    @BeforeEach
    void setUpConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, String> cf =
                new DefaultKafkaConsumerFactory<>(props);

        ContainerProperties containerProps = new ContainerProperties(TOPIC);
        containerProps.setMessageListener((MessageListener<String, String>) record -> {
            consumedMessage.set(record.value());
            latch.countDown();
        });

        container = new ConcurrentMessageListenerContainer<>(cf, containerProps);
        container.start();
    }

    @AfterEach
    void tearDownConsumer() {
        if (container != null) {
            container.stop();
        }
    }

    @Test
    void produceAndConsumeTest() throws Exception {
        // given
        String payload = "{\"orderId\": 100, \"orderStatus\": \"CREATED\"}";

        // when
        kafkaTemplate.send(TOPIC, payload);

        // then
        boolean consumed = latch.await(5, TimeUnit.SECONDS);
        Assertions.assertNotNull(consumedMessage.get());
        assertTrue(consumedMessage.get().contains("\"orderId\": 100"));
    }

    @Test
    void manualRecovererAccept_shouldInsertFailedConsumerOutboxEvent() throws Exception {
        // given: 선행 OutboxEvent insert (원본 데이터)
        OutboxEvent original = OutboxEvent.builder()
                .aggregateType("ORDER")
                .aggregateId("200")
                .eventType("ORDER_CREATED")
                .payload("{\"orderId\":200,\"orderStatus\":\"CREATED\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        original = outboxRepository.save(original);  // save 후 id 획득

        // 실패 시 보내질 메시지 (outboxEventId 포함)
        String jsonPayload = String.format("{\"orderId\":200,\"orderStatus\":\"CREATED\",\"outboxEventId\":%d}", original.getId());

        // Kafka ConsumerRecord mock 구성
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "order.created.v1", // topic
                0,                  // partition
                0L,                 // offset
                "key",              // key
                jsonPayload         // value
        );

        // 커스텀 Recoverer 생성
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, ex) -> new TopicPartition("order.created.v1.DLQ", r.partition())) {
            @Override
            public void accept(ConsumerRecord<?, ?> r, Exception ex) {
                super.accept(r, ex);
                try {
                    String value = r.value().toString();
                    JsonNode node = new ObjectMapper().readTree(value);
                    Long outboxEventId = node.get("outboxEventId").asLong();

                    OutboxEvent originalEvent = outboxRepository.findById(outboxEventId);

                    OutboxEvent failed = OutboxEvent.builder()
                            .aggregateType(originalEvent.getAggregateType())
                            .aggregateId(originalEvent.getAggregateId())
                            .eventType(originalEvent.getEventType())
                            .payload(originalEvent.getPayload())
                            .status(OutboxStatus.FAILED_CONSUMER)
                            .build();

                    outboxRepository.save(failed);
                    log.info("🟠 DLQ 처리 완료, ID = {}", failed.getId());

                } catch (Exception e) {
                    log.error("DLQ 처리 실패", e);
                }
            }
        };

        // when: 예외와 함께 recoverer 호출
        recoverer.accept(record, new RuntimeException("강제 실패"));

        // then: 새로운 FAILED_CONSUMER 이벤트가 저장되었는지 확인
        List<OutboxEvent> failedList = outboxRepository.findByStatus(OutboxStatus.FAILED_CONSUMER);
        assertFalse(failedList.isEmpty(), "FAILED_CONSUMER 이벤트가 저장되어야 합니다");
        log.info("DLQ 저장 확인: {}", failedList.get(0));
    }
}
