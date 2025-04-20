package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "쿠폰 조회 Response DTO")
public record CouponResponseDTO (
        @Schema(description = "쿠폰 ID") Long couponId,
        @Schema(description = "쿠폰 명") String couponName,
        @Schema(description = "할인유형") CouponDiscountType discountType,
        @Schema(description = "할인금액") Long discountValue,
        @Schema(description = "쿠폰 상태") CouponStatus status,
        @Schema(description = "발급일자") LocalDateTime issuedDt,
        @Schema(description = "사용일자") LocalDateTime usedDt,
        @Schema(description = "시작일자") LocalDateTime efctStDt,
        @Schema(description = "종료일자") LocalDateTime efctFnsDt
) {

    public static CouponResponseDTO from(Coupon coupon, CouponIssue couponIssue) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                couponIssue.getStatus(),
                couponIssue.getIssuedDt(),
                couponIssue.getUsedDt(),
                coupon.getEfctStDt(),
                coupon.getEfctFnsDt()
        );
    }
}
