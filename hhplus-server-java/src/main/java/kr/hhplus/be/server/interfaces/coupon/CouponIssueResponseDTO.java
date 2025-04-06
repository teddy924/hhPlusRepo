package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 발급 응답 DTO")
public class CouponIssueResponseDTO {
    private boolean success;
    private Long couponIssueId;
    private LocalDateTime issuedAt;
}
