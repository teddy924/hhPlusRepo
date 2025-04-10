package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ProductRankingPolicy {

    public static final int LOOKBACK_DAYS = 3;
    public static final int TOP_COUNT = 5;

    public LocalDateTime getStartTime() {
        return LocalDate.now().minusDays(LOOKBACK_DAYS).atStartOfDay();
    }

    public LocalDateTime getEndTime() {
        return LocalDate.now().minusDays(1).atTime(23, 59, 59);
    }

}
