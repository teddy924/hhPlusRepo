package kr.hhplus.be.server.coupon.entity;

import jakarta.persistence.*;
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
    private long id;

    private long userId;

    private long couponId;

    private Enum<CouponStatus> status;

    private LocalDateTime issuedDt;

    private LocalDateTime uesdDt;
}
