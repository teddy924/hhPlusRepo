package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import lombok.Builder;


@Builder
@Schema(description = "쿠폰 발급 Request DTO")
public record CouponIssueRequestDTO (
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "쿠폰 ID") Long couponId
) {
    public CouponIssueCommand toCommand() {
        return new CouponIssueCommand(userId, couponId);
    }
}
