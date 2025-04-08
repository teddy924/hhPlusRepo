package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
