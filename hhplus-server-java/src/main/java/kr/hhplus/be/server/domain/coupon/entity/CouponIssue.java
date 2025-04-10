package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Getter
@Entity
@Table(name = "coupon_issues")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long couponId;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime issuedDt;

    private LocalDateTime usedDt;

    public void restore() {
        if (this.status != CouponStatus.USED) {
            throw new CustomException(INVALID_COUPON_RESTORE); // 이미 사용하지 않은 쿠폰은 복구 대상이 아님
        }

        this.status = CouponStatus.ISSUED;   // 사용 → 미사용
        this.usedDt = null;                  // 사용일자 초기화
    }
}
