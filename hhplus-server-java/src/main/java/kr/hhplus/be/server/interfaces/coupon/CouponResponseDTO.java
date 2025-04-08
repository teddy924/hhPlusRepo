package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.CouponQueryDto;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
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

    public static CouponResponseDTO from(CouponQueryDto couponQueryDto) {
        return new CouponResponseDTO(
                couponQueryDto.couponId(),
                couponQueryDto.couponName(),
                couponQueryDto.discountType(),
                couponQueryDto.discountValue(),
                couponQueryDto.status(),
                couponQueryDto.issuedDt(),
                couponQueryDto.usedDt()
        );
    }
}
