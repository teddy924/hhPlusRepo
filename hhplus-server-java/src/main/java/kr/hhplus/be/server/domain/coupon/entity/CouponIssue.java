package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.application.coupon.CouponInfo;
import kr.hhplus.be.server.application.coupon.CouponIssueInfo;
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

    // 쿠폰 복구
    public void restore() {
        if (this.status != CouponStatus.USED) {
            throw new CustomException(INVALID_COUPON_RESTORE); // 이미 사용하지 않은 쿠폰은 복구 대상이 아님
        }

        this.status = CouponStatus.ISSUED;   // 사용 → 미사용
        this.usedDt = null;                  // 사용일자 초기화
    }

    // 쿠폰 발급
    public CouponIssue issue(CouponIssueInfo issueInfo) {

        isIssue(issueInfo);

        return CouponIssue.builder()
                .userId(issueInfo.userId())
                .couponId(issueInfo.couponId())
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .usedDt(null)
                .build();
    }

    // 쿠폰 중복 발급 확인
    public void isIssue(CouponIssueInfo issueInfo) {
        if (!issueInfo.couponIssueList().isEmpty()) {
            issueInfo.couponIssueList().forEach(issue -> {
                boolean isDuplicate = issue.getCouponId().equals(issueInfo.couponId());
                if (isDuplicate) {
                    throw new CustomException(DUPLICATE_ISSUE_COUPON);
                }
            });
        }
    }

    // 쿠폰 사용
    public CouponIssue use(CouponInfo couponInfo) {
        // 사용 이력 검사
        isUsed(couponInfo.couponIssue());

        return CouponIssue.builder()
                .id(couponInfo.couponIssue().getId())
                .userId(couponInfo.couponIssue().getUserId())
                .couponId(couponInfo.couponIssue().getCouponId())
                .status(CouponStatus.USED)
                .issuedDt(couponInfo.couponIssue().getIssuedDt())
                .usedDt(LocalDateTime.now())
                .build();
    }

    // 쿠폰 사용 이력 확인
    public void isUsed(CouponIssue couponIssue) {
        if (couponIssue.getStatus() == CouponStatus.USED) {
            throw new CustomException(ALREADY_USED_COUPON);
        }
    }
}
