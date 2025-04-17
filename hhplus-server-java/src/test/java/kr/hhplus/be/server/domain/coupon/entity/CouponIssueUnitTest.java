package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.domain.coupon.CouponDiscountType.RATE;
import static org.junit.jupiter.api.Assertions.*;

class CouponIssueUnitTest {

    @Test
    @DisplayName("쿠폰이 사용된 상태일 때 복구 시 상태가 ISSUED로 바뀌고 usedDt는 null이 되어야 한다")
    void restore_shouldResetUsedStatus() {
        // given
        User user = User.builder()
                .id(1L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();
        Coupon coupon = Coupon.builder().id(10L).name("10% 할인").discountType(RATE).discountValue(10L)
                .limitQuantity(3000).remainQuantity(1500)
                .efctStDt(LocalDateTime.now().minusDays(5))
                .efctFnsDt(LocalDateTime.now().plusDays(30))
                .sysCretDt(LocalDateTime.now().minusDays(5))
                .build();
        CouponIssue issue = CouponIssue.builder()
                .id(1L)
                .user(user)
                .coupon(coupon)
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
        User user = User.builder()
                .id(1L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();
        Coupon coupon = Coupon.builder().id(10L).name("10% 할인").discountType(RATE).discountValue(10L)
                .limitQuantity(3000).remainQuantity(1500)
                .efctStDt(LocalDateTime.now().minusDays(5))
                .efctFnsDt(LocalDateTime.now().plusDays(30))
                .sysCretDt(LocalDateTime.now().minusDays(5))
                .build();
        CouponIssue issue = CouponIssue.builder()
                .id(2L)
                .user(user)
                .coupon(coupon)
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .build();

        // when & then
        CustomException ex = assertThrows(CustomException.class, issue::restore);
        assertTrue(ex.getMessage().contains("복구 대상 쿠폰이 아닙니다."));
    }
}
