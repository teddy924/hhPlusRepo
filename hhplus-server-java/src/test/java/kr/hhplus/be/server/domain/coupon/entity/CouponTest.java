package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("쿠폰 수량이 0이면 isExhausted는 true를 반환한다")
    void isExhausted_shouldReturnTrue() {
        Coupon coupon = Coupon.builder().remainQuantity(0).build();

        assertTrue(coupon.isExhausted());
    }

    @Test
    @DisplayName("쿠폰 수량이 1 이상이면 isExhausted는 false를 반환한다")
    void isExhausted_shouldReturnFalse() {
        Coupon coupon = Coupon.builder().remainQuantity(5).build();

        assertFalse(coupon.isExhausted());
    }

    @Test
    @DisplayName("쿠폰 사용 시 수량이 차감된다")
    void useOneQuantity_shouldDecreaseRemain() {
        Coupon coupon = Coupon.builder().remainQuantity(3).build();

        coupon.useOneQuantity();

        assertEquals(2, coupon.getRemainQuantity());
    }

    @Test
    @DisplayName("쿠폰 수량이 0이면 사용 시 예외가 발생한다")
    void useOneQuantity_shouldThrow_whenExhausted() {
        Coupon coupon = Coupon.builder().remainQuantity(0).build();

        CustomException ex = assertThrows(CustomException.class, coupon::useOneQuantity);

        assertTrue(ex.getMessage().contains("해당 쿠폰 재고가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("현재가 유효기간 안에 있으면 expiredCoupon 통과")
    void expiredCoupon_shouldPass_whenWithinPeriod() {
        Coupon coupon = Coupon.builder()
                .efctStDt(LocalDateTime.now().minusDays(1))
                .efctFnsDt(LocalDateTime.now().plusDays(1))
                .build();

        assertDoesNotThrow(coupon::expiredCoupon);
    }

    @Test
    @DisplayName("현재가 유효기간 밖이면 expiredCoupon에서 예외 발생")
    void expiredCoupon_shouldThrow_whenOutOfPeriod() {
        Coupon coupon = Coupon.builder()
                .efctStDt(LocalDateTime.now().minusDays(5))
                .efctFnsDt(LocalDateTime.now().minusDays(1))
                .build();

        CustomException ex = assertThrows(CustomException.class, coupon::expiredCoupon);

        assertTrue(ex.getMessage().contains("유효하지 않은 쿠폰입니다"));
    }

    @Test
    @DisplayName("정액 할인 쿠폰은 할인 금액을 그대로 반환 (단, 최대 price까지)")
    void calculateCoupon_shouldReturnFixedAmount() {
        Coupon coupon = Coupon.builder()
                .discountType(CouponDiscountType.AMOUNT)
                .discountValue(1000L)
                .build();

        Long result = coupon.calculateCoupon(800L); // 가격보다 할인금액 큼 → 가격만큼만 할인
        assertEquals(800L, result);
    }

    @Test
    @DisplayName("정률 할인 쿠폰은 비율만큼 할인된 금액을 반환")
    void calculateCoupon_shouldReturnRateDiscount() {
        Coupon coupon = Coupon.builder()
                .discountType(CouponDiscountType.RATE)
                .discountValue(20L)
                .build();

        Long result = coupon.calculateCoupon(10000L); // 20% → 2000원 할인
        assertEquals(2000L, result);
    }

    @Test
    @DisplayName("지원하지 않는 할인 타입일 경우 예외 발생")
    void calculateCoupon_shouldThrow_whenInvalidType() {
        Coupon coupon = Coupon.builder()
                .discountType(null)
                .build();

        CustomException ex = assertThrows(CustomException.class, () -> coupon.calculateCoupon(1000L));

        assertTrue(ex.getMessage().contains("유효하지 않은 쿠폰"));
    }
}
