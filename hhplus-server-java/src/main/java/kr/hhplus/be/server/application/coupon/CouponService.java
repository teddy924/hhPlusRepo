package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponIssueRequestDTO;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    public List<CouponResponseDTO> retrieveCouponList (Long userId) {

        // 1. 발급 이력 조회
        List<CouponIssue> couponIssueList =  couponIssueRepository.findAllByUserId(userId);

        if (couponIssueList.isEmpty()) {
            throw new CustomException(NOT_HAS_COUPON);
        }

        // 2. 필요한 쿠폰 ID 목록 추출
        List<Long> couponIds = couponIssueList.stream()
                .map(CouponIssue::getCouponId)
                .distinct()
                .toList();

        // 3. 쿠폰 ID로 쿠폰 조회
        Map<Long, Coupon> couponMap = couponRepository.findByCouponIds(couponIds).stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));

        // 4. 쿠폰 이력 → CouponQueryDto 리스트로 변환
        return couponIssueList.stream()
                .map(issue -> {
                    Coupon coupon = couponMap.get(issue.getCouponId());
                    return CouponResponseDTO.from(coupon, issue);
                })
                .toList();

    }

    public void issueCoupon (CouponIssueRequestDTO couponIssueRequestDTO) {

        // 1. 쿠폰 유효여부 확인
        Optional<Coupon> couponOpt = couponRepository.findById(couponIssueRequestDTO.userId());

        Coupon coupon = couponOpt.orElseThrow(() -> new CustomException(NOT_EXIST_COUPON));

        // 쿠폰 유효기간 유효 여부
        coupon.expiredCoupon();

        // 2. 쿠폰 기 발급 여부 확인
        List<CouponIssue> couponIssue = couponIssueRepository.findAllByUserId(couponIssueRequestDTO.userId());

        if (!couponIssue.isEmpty()) {
            couponIssue.forEach(issue -> {
                boolean isDuplicate = issue.getCouponId().equals(couponIssueRequestDTO.couponId());
                if (isDuplicate) {
                    throw new CustomException(DUPLICATE_ISSUE_COUPON);
                }
            });
        }

        // 3. 쿠폰 발급
        coupon.useOneQuantity();
        couponRepository.save(coupon);

        CouponIssue issue = CouponIssue.builder()
                .userId(couponIssueRequestDTO.userId())
                .couponId(couponIssueRequestDTO.couponId())
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .usedDt(null)
                .build();

        // 4. 쿠폰 발급 이력 저장
        couponIssueRepository.save(issue);

    }

}
