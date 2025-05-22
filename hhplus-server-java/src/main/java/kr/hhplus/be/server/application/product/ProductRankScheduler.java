package kr.hhplus.be.server.application.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRankSnapshotRepository;
import kr.hhplus.be.server.domain.product.entity.ProductRankSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ProductRankScheduler {
    private final ProductFacade productFacade;
    private final ProductRankSnapshotRepository productRankSnapshotRepository;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public ProductRankScheduler(ProductFacade productFacade,
                                ProductRankSnapshotRepository productRankSnapshotRepository,
                                RedissonClient redissonClient,
                                ObjectMapper objectMapper) {
        this.productFacade = productFacade;
        this.productRankSnapshotRepository = productRankSnapshotRepository;
        this.redissonClient = redissonClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 0 * * * *") // 1시간마다 예시
    @Transactional
    public void updateProductRankSnapshot() {
        LocalDateTime snapshotAt = LocalDateTime.now();

        // 1. 카테고리 목록 추출
        List<String> categories = new ArrayList<>(Arrays.stream(ProductCategoryType.values())
                .map(Enum::name)
                .toList());
        categories.add("ALL");

        for (String category : categories) {
            // 2. 기존 snapshot 데이터 삭제
            productRankSnapshotRepository.deleteByCategoryAndSnapshotAt(category, snapshotAt);

            // 3. 카테고리 별 top5 집계
            List<ProductSalesResult> rankList = productFacade.retrieveRank("ALL".equals(category) ? null : category);

            // 4. 스냅샷 저장
            for (int i = 0; i < rankList.size(); i++) {
                ProductSalesResult result = rankList.get(i);
                productRankSnapshotRepository.save(
                        ProductRankSnapshot.from(result, category, i + 1, snapshotAt)
                );
            }

            // 5. Redis 캐시 저장 (Redisson)
            try {
                String key = CacheKey.ranking(category);
                String json = objectMapper.writeValueAsString(rankList);
                redissonClient.<String>getBucket(key).set(json);
                log.info("update cacheKey: {}", key);
                log.info("update rank: {}", json);
            } catch (JsonProcessingException e) {
                log.error("Redis 캐싱 실패 - category: {}", category, e);
            }
        }

        log.info("상품 랭킹 스냅샷 집계 완료 (시각: {})", snapshotAt);
    }
}
