package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.domain.coupon.CouponDiscountType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponIssueRepository couponIssueRepository;

    @Test
    @DisplayName("보유 쿠폰 조회 성공")
    void retrieveCouponList_success() {
        // given
        Long userId = 1L;
        CouponIssue issue1 = new CouponIssue(1L, userId, 10L, CouponStatus.ISSUED, LocalDateTime.now(), null);
        CouponIssue issue2 = new CouponIssue(2L, userId, 11L, CouponStatus.ISSUED, LocalDateTime.now(), null);
        Coupon coupon1 = new Coupon(10L, "10% 할인", RATE, 10L, 3000, 1500,LocalDateTime.now().minusDays(5L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(5L), null);
        Coupon coupon2 = new Coupon(11L, "20% 할인", RATE, 20L, 2000, 1500,LocalDateTime.now().minusDays(5L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(5L), null);
        Coupon coupon3 = new Coupon(12L, "30% 할인", RATE, 30L, 1000, 500,LocalDateTime.now().minusDays(5L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(5L), null);

        when(couponIssueRepository.findAllByUserId(userId)).thenReturn(List.of(issue1, issue2));
        when(couponRepository.findByCouponIds(List.of(10L, 11L))).thenReturn(List.of(coupon1, coupon2));

        // when
        List<CouponResponseDTO> result = couponService.retrieveCouponList(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getCouponId());
    }

    @Test
    @DisplayName("보유 쿠폰이 없는 경우 예외 발생")
    void retrieveCouponList_shouldThrow_whenEmpty() {
        when(couponIssueRepository.findAllByUserId(1L)).thenReturn(List.of());

        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.retrieveCouponList(1L)
        );

        assertTrue(ex.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("쿠폰 발급 시 쿠폰이 존재하지 않으면 예외 발생")
    void issueCoupon_shouldThrow_whenCouponNotFound() {
        CouponIssueCommand couponIssueCommand = new CouponIssueCommand(1L, 1L);
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.issueCoupon(couponIssueCommand)
        );

        assertTrue(ex.getMessage().contains("해당 쿠폰을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("유효기간이 만료된 쿠폰을 발급할 경우 예외가 발생해야 한다")
    void issueCoupon_shouldThrow_whenCouponExpired() {
        // given
        CouponIssueCommand couponIssueCommand = new CouponIssueCommand(1L, 10L);

        Coupon expiredCoupon = new Coupon(
                10L,
                "만료된 쿠폰",
                RATE,
                10L,
                3000,
                1500,
                LocalDateTime.now().minusDays(10L), // 시작일
                LocalDateTime.now().minusDays(5L),  // 종료일 (이미 지남)
                LocalDateTime.now().minusDays(5L),
                null
        );

        when(couponRepository.findById(10L)).thenReturn(Optional.of(expiredCoupon));

        // when
        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.issueCoupon(couponIssueCommand)
        );

        // then
        assertTrue(ex.getMessage().contains("유효하지 않은 쿠폰입니다."));
    }

    @Test
    @DisplayName("쿠폰 복구 시 존재하지 않으면 예외 발생")
    void restoreCoupon_shouldThrow_whenNotFound() {
        when(couponIssueRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.restoreCouponUsage(1L, 10L)
        );

        assertTrue(ex.getMessage().contains("해당 쿠폰을 찾을 수 없습니다."));
    }
}
