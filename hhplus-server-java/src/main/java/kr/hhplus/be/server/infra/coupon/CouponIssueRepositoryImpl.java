package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponIssueRepositoryImpl implements CouponIssueRepository {

    @Override
    public List<CouponIssue> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void save(CouponIssue couponIssue) {

    }
}
