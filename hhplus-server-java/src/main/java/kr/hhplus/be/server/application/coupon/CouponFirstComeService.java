package kr.hhplus.be.server.application.coupon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.swagger.ErrorCode;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.infra.outbox.OutboxEvent;
import kr.hhplus.be.server.infra.outbox.OutboxService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class CouponFirstComeService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedissonClient redissonClient;
    private final CouponCacheManager couponCacheManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public CouponFirstComeService(CouponRepository couponRepository,
                                  CouponIssueRepository couponIssueRepository,
                                  RedissonClient redissonClient,
                                  CouponCacheManager couponCacheManager,
                                  ApplicationEventPublisher applicationEventPublisher, OutboxService outboxService, ObjectMapper objectMapper) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.redissonClient = redissonClient;
        this.couponCacheManager = couponCacheManager;
        this.applicationEventPublisher = applicationEventPublisher;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void issueCouponFirstCome(CouponIssueCommand command) {
        Long userId = command.userId();
        Long couponId = command.couponId();

        Coupon coupon = couponRepository.getById(couponId);
        coupon.expiredCoupon(); // 유효기간 확인

        String stockKey = CacheKey.stock(couponId);             // 정렬을 위해 zset 사용
        String issuedSetKey = CacheKey.issuedSet(couponId);     // 발급여부는 순서와 무관하므로 set 사용
        log.info("stockKey:{}", stockKey);
        log.info("issuedSetKey:{}", issuedSetKey);

        // 중복 발급 여부 체크
        couponCacheManager.checkDuplicateIssue(issuedSetKey, userId);

        // 발급 시도 (선착순 삽입)
        couponCacheManager.tryAddToStock(stockKey, userId);

        // 발급 인원 초과 시 제거
        couponCacheManager.checkStockOverflow(stockKey, userId, coupon.getLimitQuantity());

        // DB 기준 실제 재고 검증
        couponCacheManager.checkStockRemain(stockKey, userId, coupon.getRemainQuantity());

        // 쿠폰 발급 성공 시
        couponCacheManager.registerCouponToActiveSet(couponId);

//        // 발급 이력 저장 (DB) - async
//        saveCouponIssueAsnyc(command);

        // 이벤트 처리 - 주문 외부 플랫폼 전송
//        applicationEventPublisher.publishEvent(CouponIssueCommand.builder()
//                .userId(userId)
//                .couponId(couponId)
//                .build());

        saveCouponIssueKafka(CouponIssueCommand.builder()
                .userId(userId)
                .couponId(couponId)
                .build());

        // 🌟 refactoring - TTL 설정 - 쿠폰 유효성 검증
        couponCacheIssuedUser(issuedSetKey, userId, coupon.getEfctFnsDt());
    }

    @Async
    public void saveCouponIssueAsnyc(CouponIssueCommand command) {
        CouponIssue issue = CouponIssue.create(command.toInfo());
        couponIssueRepository.save(issue);
    }

    public void couponCacheIssuedUser(String issuedSetKey, Long userId, LocalDateTime efctFnsDt) {
        RSet<String> issuedSet = redissonClient.getSet(issuedSetKey);
        issuedSet.add(userId.toString());                                       // 발급이력 set에 유저 add

        Duration ttl = Duration.between(LocalDateTime.now(), efctFnsDt);        // 현재 시각과 쿠폰종료시각의 차이를 계산
        if (!ttl.isNegative()) {                                                // 음수 : 이미 쿠폰 만료 | 양수 : ttl이 남은 기간
            issuedSet.expire(ttl);                                              // 남은 기간 뒤에 자동으로 cache 삭제
        }
    }

    public void saveCouponIssueKafka(CouponIssueCommand command) {
        try {
            // 1. ID 확보용 임시 저장
            OutboxEvent tmp = outboxService.saveOutboxEvent(
                    "COUPON",
                    command.userId().toString(),
                    "COUPON_ISSUED",
                    "TEMP"
            );

            // 2. ID 포함된 실제 command 생성
            CouponIssueCommand enriched = new CouponIssueCommand(
                    command.userId(),
                    command.couponId(),
                    tmp.getId() // ID 주입
            );

            String payload = objectMapper.writeValueAsString(command);

            outboxService.saveOutboxEvent(
                    "COUPON",
                    command.userId().toString(),
                    "COUPON_ISSUED",
                    payload
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.FAIL_SERIALIZATION);
        }
    }
}
