package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 조회 응답 DTO")
public class CouponResponseDTO {

    private Long couponId;
    private String couponName;
    private CouponDiscountType discountType;
    private Long discountValue;
    private CouponStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

    public static CouponResponseDTO from(Coupon coupon, CouponIssue couponIssue) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                couponIssue.getStatus(),
                couponIssue.getIssuedDt(),
                couponIssue.getUsedDt()
        );
    }
}
