package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.common.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;

import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.*;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductFacadeIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ProductFacadeIntegrationTest.class);

    @Autowired
    private ProductFacade productFacade;

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

}