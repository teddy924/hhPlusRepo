package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String discountType;
    private Long discountValue;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

}
