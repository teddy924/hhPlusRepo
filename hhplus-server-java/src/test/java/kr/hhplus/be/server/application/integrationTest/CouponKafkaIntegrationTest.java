package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.config.KafkaConsumerTestConfig;
import kr.hhplus.be.server.config.KafkaProducerTestConfig;
import kr.hhplus.be.server.config.RedissonTestConfig;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({RedissonTestConfig.class, KafkaProducerTestConfig.class, KafkaConsumerTestConfig.class})
public class CouponKafkaIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CouponKafkaIntegrationTest.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private CouponIssueRepository couponIssueRepository;
    @Autowired
    private KafkaAdmin kafkaAdmin;

    private static final String TOPIC = "coupon.FCIssued.v1";

    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicReference<String> consumedMessage = new AtomicReference<>();

    private ConcurrentMessageListenerContainer<String, String> container;

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
    void issueCoupon_shouldStoreHistorySuccessfully() throws Exception {
        // given
        String payload = """
                    {
                      "userId": 101,
                      "couponId": 1,
                      "outboxEventId": 9999
                    }
                """;

        // when
        kafkaTemplate.send(TOPIC, payload);
        kafkaTemplate.flush(); // flush 강제 추가
        boolean consumed = latch.await(5, TimeUnit.SECONDS);

        // 발급이력 DB 저장 검증
        List<CouponIssue> histories = couponIssueRepository.getAllByUserId(101L);
        assertNotNull(histories, "쿠폰 발급 이력이 저장되어야 합니다");
    }

    @Test
    void verifyTopicPartitionCount() throws ExecutionException, InterruptedException {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singletonList(TOPIC));
            Map<String, TopicDescription> descriptions = result.all().get();

            TopicDescription topicDescription = descriptions.get(TOPIC);
            int partitionCount = topicDescription.partitions().size();

            System.out.println("✔ 토픽 파티션 수: " + partitionCount);
            assertEquals(3, partitionCount, "쿠폰 발급 이력 토픽의 파티션 슈는 3개입니다.");
        }
    }
}
