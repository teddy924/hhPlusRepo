package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.order.OrderCancelCommand;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductRankScheduler;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.common.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.*;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductFacadeIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ProductFacadeIntegrationTest.class);

    @Autowired
    private ProductFacade productFacade;
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private ProductRankScheduler productRankScheduler;

    @Test
    @DisplayName("해피케이스 - 정상적으로 상위 상품 5개가 판매량 기준으로 정렬되어 리턴")
    void testHappyCase() {
        List<ProductSalesResult> result = productFacade.retrieveRank("TENT");

        Assertions.assertThat(result).hasSizeLessThanOrEqualTo(5);
        Assertions.assertThat(result).isSortedAccordingTo(Comparator.comparing(ProductSalesResult::salesQuantity).reversed());
    }

    @Test
    @DisplayName("주문은 있지만 OrderItem이 없는 경우 예외 발생")
    void testOrdersWithoutOrderItems() {
        Assertions.assertThatThrownBy(() -> productFacade.retrieveRank("FOOD"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("해당 상품 분류가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("상품 정렬은 판매수량이 높은 순으로 정렬")
    void testSalesQuantitySorting() {
        List<ProductSalesResult> result = productFacade.retrieveRank("TENT");

        Assertions.assertThat(result.size()).isGreaterThanOrEqualTo(TOP_COUNT);
    }

    @Test
    @DisplayName("결과는 최대 5개로 제한")
    void testTopCountLimit() {
        List<ProductSalesResult> result = productFacade.retrieveRank("ACC");

        Assertions.assertThat(result).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    @DisplayName("유효하지 않은 카테고리 입력 시 예외 발생")
    void testInvalidCategoryThrowsException() {
        String invalidCategory = "INVALID";

        Assertions.assertThatThrownBy(() -> productFacade.retrieveRank(invalidCategory))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("해당 상품 분류가 존재하지 않습니다.");
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
                List<ProductSalesResult> res = productFacade.retrieveRankSnapshot(null);
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

        log.debug("rank5ProductIds: {}", rank5ProductIds);

//        Assertions.assertThat(rank5ProductIds)
//                .as("동시성 테스트 결과: 조회된 TOP5 상품ID %s (취소 상품ID: %d)", rank5ProductIds, cancelProductId)
//                .contains(cancelProductId);
    }

    @BeforeAll
    void setUpAll() {
        productRankScheduler.updateProductRankSnapshot();
    }

}