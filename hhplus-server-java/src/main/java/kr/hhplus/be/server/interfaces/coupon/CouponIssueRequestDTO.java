package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.CouponIssueCommand;
import lombok.Builder;


@Builder
@Schema(description = "쿠폰 발급 요청 DTO")
public record CouponIssueRequestDTO (
        Long userId,
        Long couponId
) {
    public CouponIssueCommand toCommand() {
        return new CouponIssueCommand(userId, couponId);
    }
}
