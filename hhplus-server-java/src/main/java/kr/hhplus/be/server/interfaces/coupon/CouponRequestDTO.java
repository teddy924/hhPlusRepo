package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "쿠폰 조회 요청 DTO")
public record CouponRequestDTO (
        Long userId
) {

}
