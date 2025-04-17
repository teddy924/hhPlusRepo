package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class CouponIssueRepositoryImpl implements CouponIssueRepository {

    private final JpaCouponIssueRepository jpaCouponIssueRepository;

    @Override
    public List<CouponIssue> getAllByUserId(Long userId) {
        return jpaCouponIssueRepository.findAllByUserId(userId);
    }

    @Override
    public void save(CouponIssue couponIssue) {
        jpaCouponIssueRepository.save(couponIssue);
    }

    @Override
    public CouponIssue getByUserIdAndCouponId(Long userId, Long couponId) {
        return jpaCouponIssueRepository.findByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new CustomException(NOT_HAS_COUPON));
    }

    @Override
    public CouponIssue getByIdAndUserId(Long couponIssueId, Long userId) {
        return jpaCouponIssueRepository.findByIdAndUserId(couponIssueId, userId)
                .orElseThrow(() -> new CustomException(NOT_HAS_COUPON));
    }
}
