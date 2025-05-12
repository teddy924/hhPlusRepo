package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    // 보유 쿠폰 목록 조회
    public List<CouponResponseDTO> retrieveCouponList(Long userId) {
        // 1. 발급 이력 조회
        List<CouponIssue> couponIssueList = couponIssueRepository.getAllByUserId(userId);

        if (couponIssueList.isEmpty()) {
            throw new CustomException(NOT_HAS_COUPON);
        }

        // 2. 발급 이력에서 바로 Coupon 가져와 DTO로 매핑
        return couponIssueList.stream()
                .map(issue -> CouponResponseDTO.from(issue.getCoupon(), issue))
                .toList();
    }


    // 쿠폰 발급
    @Transactional
    public void issueCoupon(CouponIssueCommand couponIssueCommand) {
        CouponIssueInfo issueInfo = couponIssueCommand.toInfo();

        // 1. 쿠폰 유효성 확인
        Coupon coupon = couponRepository.getById(issueInfo.couponId());
        coupon.expiredCoupon(); // 유효기간 확인

        // 2. 중복 발급 확인 (CouponIssue 내부에서 책임지도록 위임)
        List<CouponIssue> alreadyIssuedList = couponIssueRepository.getAllByUserId(issueInfo.userId());
        issueInfo.addExistingIssues(alreadyIssuedList);

        CouponIssue issue = CouponIssue.create(issueInfo);

        // 3. 재고 차감
        coupon.useOneQuantity();
        couponRepository.save(coupon);

        // 4. 발급 이력 저장
        couponIssueRepository.save(issue);
    }

    // 쿠폰 조회(쿠폰 정보, 이력)
    public CouponInfo retrieveCouponInfo (CouponIssueCommand couponIssueCommand) {

        // 1. 쿠폰 발급 이력 확인
        CouponIssue issue = couponIssueRepository.getByUserIdAndCouponId(couponIssueCommand.userId(), couponIssueCommand.couponId());

        // 2. 쿠폰 엔티티 조회
        Coupon coupon = couponRepository.getById(couponIssueCommand.couponId());

        // 3. 쿠폰 유효성 검사 (만료여부 확인)
        coupon.expiredCoupon();

        return CouponInfo.builder()
                .coupon(coupon)
                .couponIssue(issue)
                .build();

    }

    // 쿠폰 사용
    public void useCoupon (CouponInfo couponInfo) {

        try {
            CouponIssue issue = couponInfo.couponIssue();
            issue.markAsUsed();
        } catch (Exception e) {
            throw new CustomException(FAIL_USE_COUPON);
        }

    }

    // 쿠폰 복구
    public void restoreCoupon(Long userId, Long couponIssueId) {

        try {
            CouponIssue issue = couponIssueRepository.getByIdAndUserId(couponIssueId, userId);
            issue.restore(); // domain method → status = ISSUED, usedDt = null
        } catch (Exception e) {
            throw new CustomException(FAIL_RESTORE_COUPON);
        }
    }



}
