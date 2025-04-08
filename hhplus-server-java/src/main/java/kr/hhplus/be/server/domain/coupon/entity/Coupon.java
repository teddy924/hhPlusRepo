package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Getter
@Entity
@Table(name = "coupons")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CouponDiscountType discountType;

    private Long discountValue;

    private Integer limitQuantity;      // 총 발급 가능 수량

    private Integer remainQuantity;     // 남은 수량 (또는 계산식으로 처리)

    private LocalDateTime efctStDt;

    private LocalDateTime efctFnsDt;

    private LocalDateTime sysCretDt;

    private LocalDateTime sysChgDt;


    public boolean isExhausted() {
        return remainQuantity != null && remainQuantity <= 0;
    }

    public void useOneQuantity() {
        if (isExhausted()) {
            throw new CustomException(COUPON_SOLD_OUT);
        }
        this.remainQuantity -= 1;
    }

    public void expiredCoupon() {
        LocalDateTime now = LocalDateTime.now();
        if (!(now.isAfter(efctStDt) && now.isBefore(efctFnsDt))) {
            throw new CustomException(INVALID_COUPON);
        }
    }

}
