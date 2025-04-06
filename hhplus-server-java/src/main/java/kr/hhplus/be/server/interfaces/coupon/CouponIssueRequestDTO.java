package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "쿠폰 발급 요청 DTO")
public class CouponIssueRequestDTO {

    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "쿠폰 ID", example = "130201020")
    private Long couponId;

}
