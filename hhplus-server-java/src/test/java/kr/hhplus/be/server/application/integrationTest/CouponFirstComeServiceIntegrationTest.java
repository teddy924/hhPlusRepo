package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.coupon.CouponFirstComeService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.RedissonTestConfig;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import org.junit.jupiter.api.*;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@Import(RedissonTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouponFirstComeServiceIntegrationTest {

    @Autowired
    CouponFirstComeService couponFirstComeService;

    @Autowired
    RedissonClient redissonClient;

    String stockZsetKey = "coupon:stock:zset:9";
    String issueSetKey = "coupon:issue:set:9";

    @BeforeAll
    void clearRedis() {
        redissonClient.getBucket(stockZsetKey).delete();
        redissonClient.getBucket(issueSetKey).delete();
    }

    @Test
    @DisplayName("선착순 쿠폰 발급 성공")
    void issueCouponFirstCome_success() {
        // given
        Long userId = 1001L;
        Long couponId = 9L;

        CouponIssueCommand command = new CouponIssueCommand(userId, couponId);

        // when
        couponFirstComeService.issueCouponFirstCome(command);

        // then
        Boolean isMember = redissonClient.getSet(issueSetKey).contains(userId.toString());
        assertEquals(Boolean.TRUE, isMember, "발급 이력에 사용자 포함");

        Integer rank = redissonClient.getScoredSortedSet(stockZsetKey).rank(userId.toString());
        assertNotNull(rank, "ZSet에 사용자 순위 존재");
    }

    @Test
    @DisplayName("중복 발급 시도 예외")
    void issueCouponFirstCome_duplicateIssue() {
        // given
        Long userId = 1001L;
        Long couponId = 9L;

        redissonClient.getSet(issueSetKey).add(userId);

        CouponIssueCommand command = new CouponIssueCommand(userId, couponId);

        // expect
        CustomException ex = assertThrows(CustomException.class, () -> couponFirstComeService.issueCouponFirstCome(command));
        assertEquals("이미 발급 받은 쿠폰입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("쿠폰 재고 초과 시 예외")
    void issueCouponFirstCome_stockExceeded() {
        // given
        Long couponId = 700002L;
        Long lateUserId = 1002L;
        CouponIssueCommand command = new CouponIssueCommand(lateUserId, couponId);

        // expect
        CustomException ex = assertThrows(CustomException.class, () -> couponFirstComeService.issueCouponFirstCome(command));
        assertEquals("해당 쿠폰 재고가 존재하지 않습니다.", ex.getMessage());
    }

}
