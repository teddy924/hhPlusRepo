package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.order.OrderCancelCommand;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.product.*;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.EmbeddedRedisConfig;
import kr.hhplus.be.server.config.redis.RedisSlaveSelector;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedRedisConfig.class)
class ProductServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceIntegrationTest.class);

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    @Qualifier("masterRedisTemplate")
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisSlaveSelector redisSlaveSelector;

    @Autowired
    ProductRankScheduler productRankScheduler;

    @Autowired
    OrderFacade orderFacade;

    @Test
    @DisplayName("카테고리 없이 전체 상품 조회")
    void retrieveAll_shouldReturnAllProducts_whenCategoryIsNull() {
        List<ProductResult> results = productService.retrieveAll(null);

        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("유효한 카테고리로 상품 조회")
    void retrieveAll_shouldFilterByCategory() {
        List<ProductResult> results = productService.retrieveAll("TENT");

        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(p -> p.category().name().equals("TENT")));
    }

    @Test
    @DisplayName("잘못된 카테고리 입력 시 예외 발생")
    void retrieveAll_shouldThrow_whenInvalidCategory() {
        CustomException ex = assertThrows(CustomException.class, () -> productService.retrieveAll("INVALID"));
        assertTrue(ex.getMessage().contains("해당 상품 분류가 존재하지 않습니다."));

    }

    @Test
    @DisplayName("존재하는 상품 ID로 상세 조회")
    void retrieveDetail_shouldReturnProduct() {
        Long productId = 1L;

        ProductResult result = productService.retrieveDetail(productId);

        assertEquals(productId, result.productId());
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생")
    void retrieveDetail_shouldThrowException_whenProductNotFound() {
        Long invalidId = 9999L;

        CustomException ex = assertThrows(CustomException.class, () -> productService.retrieveDetail(invalidId));
        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("정상적으로 재고 차감")
    void decreaseStock_shouldDecreaseCorrectly() {
        Long productId = 2L;
        int quantity = 2;

        Product before = productRepository.findById(productId);
        int beforeStock = before.getStock();
        log.debug("before stock: {}", beforeStock);

        productService.decreaseStock(productId, quantity);

        Product after = productRepository.findById(productId);
        assertEquals(beforeStock - quantity, after.getStock());
    }

    @Test
    @DisplayName("재고가 부족할 때 예외 발생")
    void decreaseStock_shouldThrowException_whenInsufficient() {
        Long productId = 3L;
        int quantity = 99999;

        CustomException ex = assertThrows(CustomException.class, () -> productService.decreaseStock(productId, quantity));
        assertTrue(ex.getMessage().contains("상품이 품절 상태 입니다."));
    }

    @Test
    @DisplayName("정상적으로 재고 복구")
    void restoreStock_shouldIncreaseCorrectly() throws Exception {
        Long productId = 4L;
        int quantity = 5;

        Product before = productRepository.findById(productId);
        int beforeStock = before.getStock();
        log.debug("before stock: " + before.getStock());

        productService.restoreStock(productId, quantity);

        Product after = productRepository.findById(productId);
        assertEquals(beforeStock + quantity, after.getStock());
    }

    @Test
    @DisplayName("존재하지 않는 상품 포함 시 예외 발생")
    void processOrderProducts_shouldThrow_whenInvalidProduct() {
        Map<Long, Integer> orderMap = Map.of(
                1L, 2,
                9999L, 1
        );

        CustomException ex = assertThrows(CustomException.class, () -> productService.processOrderProducts(orderMap));
        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));

    }

    @Test
    @DisplayName("상품 상세조회 시 캐시 저장 후 슬레이브에서 조회되는지 확인")
    void retrieveDetail_shouldCacheToMaster_andBeVisibleToSlave() throws Exception {
        Long productId = 21L;
        String cacheKey = "product:" + productId;

        redisTemplate.delete(cacheKey); // 캐시 제거

        productService.retrieveDetail(productId); // 캐시 생성

        RedisTemplate<String, Object> slaveRedis = redisSlaveSelector.getRandomSlave();
        Object cached = slaveRedis.opsForValue().get(cacheKey);

        assertNotNull(cached, "슬레이브 캐시에 데이터가 있어야 함");
        assertInstanceOf(ProductResult.class, cached);
        assertEquals(productId, ((ProductResult) cached).productId());
    }

    @Test
    @DisplayName("최근 3일 상위 상품 조회와 그 일자에 해당하는 주문의 취소 동시성 이슈 테스트")
    void rank5Product_concurrencyWithCancelOrder() throws InterruptedException {
        Long cancelOrderId = 600001L;
        Long cancelProductId = 900002L;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);  // 동시에 시작시키는 용도
        CountDownLatch doneLatch = new CountDownLatch(2);   // 모든 작업 종료 대기용

        List<Long> rank5ProductIds = Collections.synchronizedList(new ArrayList<>());

        // 최근 3일 상위 상품 조회
        executor.submit(() -> {
            try {
                startLatch.await();
                List<ProductSalesResult> res = productService.retrieveRankSnapshot(null);
                rank5ProductIds.addAll(res.stream().map(ProductSalesResult::productId).toList());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                doneLatch.countDown();
            }
        });

        // 주문 취소 동시 발생
        executor.submit(() -> {
            try {
                startLatch.await();
                orderFacade.cancel(new OrderCancelCommand(cancelOrderId));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                doneLatch.countDown();
            }
        });

        startLatch.countDown();

        doneLatch.await();

        log.info("rank5ProductIds: {}", rank5ProductIds);

        Assertions.assertThat(rank5ProductIds)
                .as("동시성 테스트 결과: 조회된 TOP5 상품ID %s (취소 상품ID: %d)", rank5ProductIds, cancelProductId)
                .contains(cancelProductId);
    }

    @Test
    @DisplayName("Redis에 캐시가 있으면 데이터를 정상 조회한다")
    void retrieveRankSnapshot_success() {
        List<ProductSalesResult> result = productService.retrieveRankSnapshot(null); // category = null → "ALL"

        Assertions.assertThat(result).hasSize(5);
        Assertions.assertThat(result.get(0).category()).isNotEmpty();
    }

    @Test
    void checkRedisTemplateSame() {
        System.out.println("Scheduler RedisTemplate: " + productRankScheduler.getRedisTemplate());
        System.out.println("Service RedisTemplate: " + productService.getRedisTemplate());

        assertEquals(productRankScheduler.getRedisTemplate(), productService.getRedisTemplate());
    }

    @Test
    @DisplayName("실시간 랭킹 조회 성공 - ZSet 기준")
    void getRealTimeRank_success() {
        String redisKey = "zset:rank:TENT";
        redisTemplate.delete(redisKey);
        redisTemplate.opsForZSet().add(redisKey, "201", 45);
        redisTemplate.opsForZSet().add(redisKey, "202", 100);
        redisTemplate.opsForZSet().add(redisKey, "203", 80);

        List<ProductRankResult> rankList = productService.getRealTimeRank("TENT");

        Assertions.assertThat(rankList).hasSize(3);
        Assertions.assertThat(rankList.get(0).productId()).isEqualTo(202L);
        Assertions.assertThat(rankList.get(1).productId()).isEqualTo(203L);
        Assertions.assertThat(rankList.get(2).productId()).isEqualTo(201L);
    }

    @BeforeAll
    void setUpAll() {
        productRankScheduler.updateProductRankSnapshot();
    }

}