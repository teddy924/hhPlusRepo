package kr.hhplus.be.server.application.coupon.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.swagger.ErrorCode;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.infra.outbox.OutboxEvent;
import kr.hhplus.be.server.infra.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventPublisher {

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public void publish(CouponIssueCommand command) {
        try {
            // 1. ID 확보용 임시 저장
            OutboxEvent tmp = outboxService.saveOutboxEvent(
                    "COUPON",
                    command.userId().toString(),
                    "COUPON_ISSUED",
                    "TEMP"
            );

            // 2. ID 포함된 실제 command 생성
            CouponIssueCommand enriched = new CouponIssueCommand(
                    command.userId(),
                    command.couponId(),
                    tmp.getId() // ID 주입
            );

            String payload = objectMapper.writeValueAsString(command);

            outboxService.saveOutboxEvent(
                    "COUPON",
                    command.userId().toString(),
                    "COUPON_ISSUED",
                    payload
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.FAIL_SERIALIZATION);
        }
    }

}
