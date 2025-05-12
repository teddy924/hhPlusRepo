package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest
@Transactional
class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Test
    @DisplayName("쿠폰 목록 조회 - 보유한 쿠폰이 있을 경우 정상 조회")
    void retrieveCouponList_shouldReturnList_whenUserHasCoupons() {
        Long userId = 31L;

        List<CouponResponseDTO> result = couponService.retrieveCouponList(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("쿠폰 목록 조회 - 보유한 쿠폰이 없으면 예외 발생")
    void retrieveCouponList_shouldThrow_whenNoCoupons() {
        Long userId = 800002L;

        CustomException ex = assertThrows(CustomException.class, () -> couponService.retrieveCouponList(userId));
        assertTrue(ex.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("쿠폰 발급 성공 - 쿠폰 수량 차감 + 이력 생성")
    void issueCoupon_shouldSucceed_whenValid() {
        CouponIssueCommand command = new CouponIssueCommand(32L, 1L);

        assertDoesNotThrow(() -> couponService.issueCoupon(command));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급받은 쿠폰")
    void issueCoupon_shouldThrow_whenAlreadyIssued() {
        CouponIssueCommand command = new CouponIssueCommand(33L, 3L);

        CustomException ex = assertThrows(CustomException.class, () -> couponService.issueCoupon(command));
        assertTrue(ex.getMessage().contains("이미 쿠폰을 받은 발급자 입니다."));
    }

    @Test
    @DisplayName("쿠폰 조회 - 유효한 발급 이력")
    void retrieveCouponInfo_shouldReturnCouponInfo() {
        CouponIssueCommand command = new CouponIssueCommand(34L, 4L);

        CouponInfo info = couponService.retrieveCouponInfo(command);

        assertEquals(4L, info.coupon().getId());
        assertEquals(34L, info. couponIssue().getUser().getId());
    }

    @Test
    @DisplayName("쿠폰 조회 실패 - 이력 없음")
    void retrieveCouponInfo_shouldThrow_whenNoIssue() {
        CouponIssueCommand command = new CouponIssueCommand(9999L, 1L);

        CustomException ex = assertThrows(CustomException.class,
                () -> couponService.retrieveCouponInfo(command));

        assertTrue(ex.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("쿠폰 사용 - 상태가 USED로 변경됨")
    void useCoupon_shouldUpdateStatusToUsed() {
        CouponIssueCommand command = new CouponIssueCommand(35L, 5L);
        CouponInfo info = couponService.retrieveCouponInfo(command);

        couponService.useCoupon(info);

        CouponInfo updated = couponService.retrieveCouponInfo(command);
        assertEquals(CouponStatus.USED, updated.couponIssue().getStatus());
    }

    @Test
    @DisplayName("쿠폰 복구 실패 - 잘못된 사용자 또는 이력 ID")
    void restoreCoupon_shouldThrow_whenInvalid() {
        CustomException ex = assertThrows(CustomException.class, () -> couponService.restoreCoupon(9999L, 9999L));
        assertTrue(ex.getMessage().contains("쿠폰 복구에 실패하였습니다."));
    }

    @Test
    @DisplayName("쿠폰 동시 발급 테스트 - 중복 발급이 실제로 발생하는지 확인")
    void concurrentCouponIssue_asIs_shouldCauseDuplicateIssue() throws InterruptedException {
        int threadCount = 10;
        Long userId = 5L;
        Long couponId = 4L;

        // 동시 요청을 위한 쓰레드풀 + 래치
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(new CouponIssueCommand(userId, couponId));
                } catch (Exception e) {
                    // 예외 무시 — 지금은 DB 결과로 판단할 것이기 때문에
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  // 모든 쓰레드 종료 대기

        // DB에서 발급된 쿠폰 이력 조회
        List<CouponIssue> issuedList = couponIssueRepository.getAllByUserId(userId);
        long issuedCount = issuedList.stream()
                .filter(issue -> issue.getCoupon().getId().equals(couponId))
                .count();

        System.out.println("실제 발급된 쿠폰 수: " + issuedCount);

        // 기대: 실제 발급된 쿠폰 수 : 1
        assertTrue(issuedCount == 1, "동시성 이슈가 발생하여 중복 발급이 됐는지 확인");
    }

    @Test
    @DisplayName("동시 발급 - 쿠폰 재고 초과 발급 동시성 이슈 확인")
    void concurrentIssueExceedingStock_shouldCauseOverIssuance() throws InterruptedException {
        int threadCount = 100;
        Long couponId = 700001L; // 재고 5개 설정된 쿠폰
        List<Long> userIds = LongStream.range(1000, 1000 + threadCount).boxed().toList();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Long userId : userIds) {
            executor.submit(() -> {
                try {
                    couponService.issueCoupon(new CouponIssueCommand(userId, couponId));
                } catch (Exception ignored) {
                    // 재고 부족 예외는 정상으로 간주
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long actualIssuedCount = couponIssueRepository.countByCouponId(couponId);

        System.out.println("실제 발급된 수량: " + actualIssuedCount);

        // 기대: 동시성 문제 발생 시 수량이 5를 초과함 / 동시성 문제 해결 시 수량은 5
        assertTrue(actualIssuedCount == 5, "동시성 이슈 해결 시 쿠폰 재고만큼 발급 됨.");
    }

}