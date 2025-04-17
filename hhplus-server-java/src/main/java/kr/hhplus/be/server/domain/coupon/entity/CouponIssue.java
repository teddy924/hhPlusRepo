package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponIssueInfo;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Getter
@Entity
@Table(name = "coupon_issue")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime issuedDt;

    private LocalDateTime usedDt;

    // 상태 변경 메서드
    public void markAsUsed() {
        if (this.status == CouponStatus.USED) {
            throw new CustomException(ALREADY_USED_COUPON);
        }
        this.status = CouponStatus.USED;
        this.usedDt = LocalDateTime.now();
    }

    public void restore() {
        if (this.status != CouponStatus.USED) {
            throw new CustomException(INVALID_COUPON_RESTORE);
        }
        this.status = CouponStatus.ISSUED;
        this.usedDt = null;
    }

    public static CouponIssue create(CouponIssueInfo issueInfo) {
        if (!issueInfo.couponIssueList().isEmpty()) {
            boolean isDuplicate = issueInfo.couponIssueList().stream()
                    .anyMatch(i -> i.getCoupon().getId().equals(issueInfo.couponId()));
            if (isDuplicate) {
                throw new CustomException(DUPLICATE_ISSUE_COUPON);
            }
        }

        return CouponIssue.builder()
                .user(User.withId(issueInfo.userId()))       // 더미 유저 객체 or null 처리
                .coupon(Coupon.withId(issueInfo.couponId())) // 더미 쿠폰 객체 or null 처리
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .build();
    }
}
