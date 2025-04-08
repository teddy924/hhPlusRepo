package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponIssueRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponService couponService;

    public void issueCoupon (CouponIssueRequestDTO couponIssueRequestDTO) {

        couponService.issueCoupon(couponIssueRequestDTO);

    }
}
