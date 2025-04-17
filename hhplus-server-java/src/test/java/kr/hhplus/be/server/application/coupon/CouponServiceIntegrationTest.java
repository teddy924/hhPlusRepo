package kr.hhplus.be.server.application.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest
@Transactional
class CouponServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 목록 조회 - 보유한 쿠폰이 있을 경우 정상 조회")
    void retrieveCouponList_shouldReturnList_whenUserHasCoupons() {
        Long userId = 1L;

        List<CouponResponseDTO> result = couponService.retrieveCouponList(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("쿠폰 목록 조회 - 보유한 쿠폰이 없으면 예외 발생")
    void retrieveCouponList_shouldThrow_whenNoCoupons() {
        Long userId = 9999L;

        CustomException ex = assertThrows(CustomException.class, () -> couponService.retrieveCouponList(userId));
        assertTrue(ex.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("쿠폰 발급 성공 - 쿠폰 수량 차감 + 이력 생성")
    void issueCoupon_shouldSucceed_whenValid() {
        CouponIssueCommand command = new CouponIssueCommand(2L, 1L);

        assertDoesNotThrow(() -> couponService.issueCoupon(command));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급받은 쿠폰")
    void issueCoupon_shouldThrow_whenAlreadyIssued() {
        CouponIssueCommand command = new CouponIssueCommand(1L, 2L);

        CustomException ex = assertThrows(CustomException.class, () -> couponService.issueCoupon(command));
        assertTrue(ex.getMessage().contains("이미 쿠폰을 받은 발급자 입니다."));
    }

    @Test
    @DisplayName("쿠폰 조회 - 유효한 발급 이력")
    void retrieveCouponInfo_shouldReturnCouponInfo() {
        CouponIssueCommand command = new CouponIssueCommand(2L, 3L);

        CouponInfo info = couponService.retrieveCouponInfo(command);

        assertEquals(3L, info.coupon().getId());
        assertEquals(2L, info. couponIssue().getUser().getId());
    }

    @Test
    @DisplayName("쿠폰 조회 실패 - 이력 없음")
    void retrieveCouponInfo_shouldThrow_whenNoIssue() {
        CouponIssueCommand command = new CouponIssueCommand(9999L, 1L);

        CustomException ex = assertThrows(CustomException.class,
                () -> couponService.retrieveCouponInfo(command));

        assertTrue(ex.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("쿠폰 사용 - 상태가 USED로 변경됨")
    void useCoupon_shouldUpdateStatusToUsed() {
        CouponIssueCommand command = new CouponIssueCommand(1L, 2L);
        CouponInfo info = couponService.retrieveCouponInfo(command);

        couponService.useCoupon(info);

        CouponInfo updated = couponService.retrieveCouponInfo(command);
        assertEquals(CouponStatus.USED, updated.couponIssue().getStatus());
    }

    @Test
    @DisplayName("쿠폰 복구 실패 - 잘못된 사용자 또는 이력 ID")
    void restoreCoupon_shouldThrow_whenInvalid() {
        CustomException ex = assertThrows(CustomException.class, () -> couponService.restoreCoupon(9999L, 9999L));
        assertTrue(ex.getMessage().contains("쿠폰 복구에 실패하였습니다."));
    }
}