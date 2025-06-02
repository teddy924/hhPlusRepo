package kr.hhplus.be.server.application.coupon.event;

import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponEventListner {

    private final CouponEventPublisher publisher;

    @TransactionalEventListener
    public void handle(CouponIssueCommand command) {
        publisher.publish(command);
    }
}
