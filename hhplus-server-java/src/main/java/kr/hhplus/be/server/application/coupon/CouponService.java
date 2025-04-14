package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 보유 쿠폰 목록 조회
    public List<CouponResponseDTO> retrieveCouponList (Long userId) {

        // 1. 발급 이력 조회
        List<CouponIssue> couponIssueList =  couponIssueRepository.findAllByUserId(userId);

        if (couponIssueList.isEmpty()) {
            throw new CustomException(NOT_HAS_COUPON);
        }

        // 2. 쿠폰 ID 목록 추출
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

    // 쿠폰 발급
    public void issueCoupon (CouponIssueCommand couponIssueCommand) {

        CouponIssueInfo issueInfo = couponIssueCommand.toInfo();

        // 1. 쿠폰 유효여부 확인
        Optional<Coupon> couponOpt = couponRepository.findById(issueInfo.couponId());

        Coupon coupon = couponOpt.orElseThrow(() -> new CustomException(NOT_EXIST_COUPON));

        // 쿠폰 유효기간 유효 여부
        coupon.expiredCoupon();

        // 2. 쿠폰 기 발급 여부 확인
        List<CouponIssue> couponIssueList = couponIssueRepository.findAllByUserId(issueInfo.userId());
        issueInfo.couponIssueList().addAll(couponIssueList);

        // 3. 쿠폰 발급
        coupon.useOneQuantity();
        couponRepository.save(coupon);

        // 4. 쿠폰 발급 이력 저장
        CouponIssue issue = new CouponIssue().issue(issueInfo);
        couponIssueRepository.save(issue);

    }

    // 쿠폰 조회(쿠폰 정보, 이력)
    public CouponInfo retrieveCouponInfo (CouponIssueCommand couponIssueCommand) {

        // 1. 쿠폰 발급 이력 확인
        CouponIssue issue = couponIssueRepository.findByUserIdAndCouponId(couponIssueCommand.userId(), couponIssueCommand.couponId())
                .orElseThrow(() -> new CustomException(NOT_HAS_COUPON));

        // 2. 쿠폰 엔티티 조회
        Coupon coupon = couponRepository.findById(couponIssueCommand.couponId())
                .orElseThrow(() -> new CustomException(NOT_EXIST_COUPON));

        // 3. 쿠폰 유효성 검사 (만료여부 확인)
        coupon.expiredCoupon();

        return CouponInfo.builder()
                .coupon(coupon)
                .couponIssue(issue)
                .build();

    }

    // 쿠폰 사용
    public void useCoupon (CouponInfo couponInfo) {

        CouponIssue issue =  new CouponIssue().use(couponInfo);

        couponIssueRepository.save(issue);

    }

    // 쿠폰 복구
    public void restoreCouponUsage(Long userId, Long couponIssueId) {
        CouponIssue issue = couponIssueRepository.findByIdAndUserId(couponIssueId, userId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_COUPON));

        issue.restore(); // domain method → status = ISSUED, usedDt = null
        couponIssueRepository.save(issue);
    }



}
