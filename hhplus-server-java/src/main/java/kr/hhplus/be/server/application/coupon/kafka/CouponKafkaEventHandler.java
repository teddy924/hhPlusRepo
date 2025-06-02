package kr.hhplus.be.server.application.coupon.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaEventHandler {

    private final ObjectMapper objectMapper;
    private final CouponIssueRepository couponIssueRepository;

    public void handleCouponIssueCommand(String message) throws Exception {
//        try {
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
//            CouponIssueCommand command = objectMapper.readValue(message, CouponIssueCommand.class);
//            CouponIssue couponIssue = CouponIssue.create(command.toInfo());
//            couponIssueRepository.save(couponIssue);
//            log.info("coupon Kafka Consumer handle command");
//        } catch (JsonProcessingException e) {
//            log.info("coupon Kafka Consumer Handle exception: {}", e.getMessage());
//            throw new RuntimeException("Kafka 메시지 역직렬화 실패", e);
//        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        CouponIssueCommand command = objectMapper.readValue(message, CouponIssueCommand.class);
        CouponIssue couponIssue = CouponIssue.create(command.toInfo());
        couponIssueRepository.save(couponIssue);
        log.info("coupon Kafka Consumer handle command");

    }

}
