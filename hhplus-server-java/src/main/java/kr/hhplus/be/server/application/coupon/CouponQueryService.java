package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponQueryService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    public CouponIssueQueryDto retrieveCouponList (Long userId) {

        // 1. 발급 이력 조회
        List<CouponIssue> couponIssueList =  couponIssueRepository.findAllByUserId(userId);

        // 2. 필요한 쿠폰 ID 목록 추출
        List<Long> couponIds = couponIssueList.stream()
                .map(CouponIssue::getCouponId)
                .distinct()
                .toList();

        // 3. 쿠폰 ID로 쿠폰 조회
        Map<Long, Coupon> couponMap = couponRepository.findByCouponIds(couponIds).stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));

        // 4. 쿠폰 이력 → CouponQueryDto 리스트로 변환
        List<CouponQueryDto> couponQueryList = couponIssueList.stream()
                .map(issue -> {
                    Coupon coupon = couponMap.get(issue.getCouponId());
                    return CouponQueryDto.from(coupon);
                })
                .toList();

        // 5. 최종 DTO 생성
        return CouponIssueQueryDto.of(userId, couponQueryList);

    }

}
