package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;

import java.util.List;

public interface CouponIssueRepository {

    List<CouponIssue> findAllByUserId(Long userId);

    void save(CouponIssue couponIssue);

}
