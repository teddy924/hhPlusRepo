package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.coupon.CouponFirstComeService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.EmbeddedRedisConfig;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@Import(EmbeddedRedisConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouponFirstComeServiceIntegrationTest {

    @Autowired
    CouponFirstComeService couponFirstComeService;

    @Qualifier("masterRedisTemplate")
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeAll
    void clearRedis() {
        redisTemplate.delete("coupon:stock:zset:9");
        redisTemplate.delete("coupon:issue:set:9");
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
        Boolean isMember = redisTemplate.opsForSet().isMember("coupon:issue:set:9", userId.toString());
        assertEquals(Boolean.TRUE, isMember, "발급 이력에 사용자 포함");

        Long rank = redisTemplate.opsForZSet().rank("coupon:stock:zset:9", userId.toString());
        assertNotNull(rank, "ZSet에 사용자 순위 존재");
    }

    @Test
    @DisplayName("중복 발급 시도 예외")
    void issueCouponFirstCome_duplicateIssue() {
        // given
        Long userId = 1001L;
        Long couponId = 9L;
        redisTemplate.opsForSet().add("coupon:issue:set:9", userId.toString());

        CouponIssueCommand command = new CouponIssueCommand(userId, couponId);

        // expect
        CustomException ex = assertThrows(CustomException.class, () -> couponFirstComeService.issueCouponFirstCome(command));
        assertEquals("중복된 쿠폰 발급 시도입니다.", ex.getMessage());
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
