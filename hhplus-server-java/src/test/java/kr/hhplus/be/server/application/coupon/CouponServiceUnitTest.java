package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.swagger.ErrorCode;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.coupon.CouponDiscountType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceUnitTest {

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
        User user = User.builder().id(userId).name("tester").build();

        Coupon coupon1 = Coupon.builder().id(10L).name("10% 할인").discountType(RATE).discountValue(10L)
                .limitQuantity(3000).remainQuantity(1500)
                .efctStDt(LocalDateTime.now().minusDays(5))
                .efctFnsDt(LocalDateTime.now().plusDays(30))
                .sysCretDt(LocalDateTime.now().minusDays(5))
                .build();

        Coupon coupon2 = Coupon.builder().id(11L).name("20% 할인").discountType(RATE).discountValue(20L)
                .limitQuantity(2000).remainQuantity(1500)
                .efctStDt(LocalDateTime.now().minusDays(5))
                .efctFnsDt(LocalDateTime.now().plusDays(30))
                .sysCretDt(LocalDateTime.now().minusDays(5))
                .build();

        CouponIssue issue1 = CouponIssue.builder()
                .id(1L)
                .user(user)
                .coupon(coupon1)
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .build();

        CouponIssue issue2 = CouponIssue.builder()
                .id(2L)
                .user(user)
                .coupon(coupon2)
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .build();

        when(couponIssueRepository.getAllByUserId(userId)).thenReturn(List.of(issue1, issue2));

        // when
        List<CouponResponseDTO> result = couponService.retrieveCouponList(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getCouponId());
    }

    @Test
    @DisplayName("보유 쿠폰이 없는 경우 예외 발생")
    void retrieveCouponList_shouldThrow_whenEmpty() {
        when(couponIssueRepository.getAllByUserId(1L)).thenReturn(List.of());

        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.retrieveCouponList(1L)
        );

        assertTrue(ex.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("쿠폰 발급 시 쿠폰이 존재하지 않으면 예외 발생")
    void issueCoupon_shouldThrow_whenCouponNotFound() {
        CouponIssueCommand couponIssueCommand = new CouponIssueCommand(1L, 1L);
        when(couponRepository.getById(anyLong()))
                .thenThrow(new CustomException(ErrorCode.NOT_EXIST_COUPON));

        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.issueCoupon(couponIssueCommand)
        );

        assertTrue(ex.getMessage().contains("해당 쿠폰을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("유효기간이 만료된 쿠폰을 발급할 경우 예외 발생")
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

        when(couponRepository.getById(10L)).thenReturn(expiredCoupon);

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
        when(couponIssueRepository.getByIdAndUserId(10L, 1L)).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class, () ->
                couponService.restoreCoupon(1L, 10L)
        );

        System.out.println(ex.getMessage());

        assertTrue(ex.getMessage().contains("쿠폰 복구에 실패하였습니다."));
    }
}
