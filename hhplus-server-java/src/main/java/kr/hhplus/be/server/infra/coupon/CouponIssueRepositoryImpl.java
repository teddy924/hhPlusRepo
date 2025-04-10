package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponIssueRepositoryImpl implements CouponIssueRepository {

    @Override
    public List<CouponIssue> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void save(CouponIssue couponIssue) {

    }

    @Override
    public Optional<CouponIssue> findByUserIdAndCouponId(Long userId, Long couponId) {
        return Optional.empty();
    }

    @Override
    public Optional<CouponIssue> findByIdAndUserId(Long couponIssueId, Long userId) {
        return Optional.empty();
    }
}
