package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponIssueUnitTest {

    @Test
    @DisplayName("쿠폰이 사용된 상태일 때 복구 시 상태가 ISSUED로 바뀌고 usedDt는 null이 되어야 한다")
    void restore_shouldResetUsedStatus() {
        // given
        CouponIssue issue = CouponIssue.builder()
                .id(1L)
                .userId(1L)
                .couponId(10L)
                .status(CouponStatus.USED)
                .issuedDt(LocalDateTime.now().minusDays(5))
                .usedDt(LocalDateTime.now())
                .build();

        // when
        issue.restore();

        // then
        assertEquals(CouponStatus.ISSUED, issue.getStatus());
        assertNull(issue.getUsedDt());
    }

    @Test
    @DisplayName("쿠폰이 미사용 상태일 때 복구를 시도하면 예외 발생")
    void restore_shouldThrow_whenStatusIsNotUsed() {
        // given
        CouponIssue issue = CouponIssue.builder()
                .id(2L)
                .userId(1L)
                .couponId(10L)
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .build();

        // when & then
        CustomException ex = assertThrows(CustomException.class, issue::restore);
        assertTrue(ex.getMessage().contains("복구 대상 쿠폰이 아닙니다."));
    }
}
