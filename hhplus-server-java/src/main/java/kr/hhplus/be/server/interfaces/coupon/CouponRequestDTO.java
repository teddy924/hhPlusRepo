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
@Schema(description = "쿠폰 조회 요청 DTO")
public class CouponRequestDTO {
    @Schema(description = "유저 ID", example = "1")
    private long userId;
}
