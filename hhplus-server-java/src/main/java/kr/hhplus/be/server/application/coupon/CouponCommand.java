package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.interfaces.coupon.CouponIssueRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponCommand {

    private final CouponService couponService;

    public void issueCoupon (CouponIssueRequestDTO couponIssueRequestDTO) {

        couponService.issueCoupon(couponIssueRequestDTO.getUserId(), couponIssueRequestDTO.getCouponId());

    }
}
