package kr.hhplus.be.server.application.coupon.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaConsumer {

    private final CouponKafkaEventHandler eventHandler;

    @KafkaListener(
            topics = "coupon.FCIssued.v1",
            groupId = "coupon.group",
            containerFactory = "couponIssueCommandKafkaListenerContainerFactory"
    )
    public void consume(String message) throws Exception {
        log.info("coupon Kafka Consumer consume");
        eventHandler.handleCouponIssueCommand(message);  // 책임 위임
    }
}
