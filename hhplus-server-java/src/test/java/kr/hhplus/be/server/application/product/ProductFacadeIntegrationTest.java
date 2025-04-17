package kr.hhplus.be.server.application.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;

import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.*;

@Testcontainers
@SpringBootTest
@Transactional
class ProductFacadeIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ProductFacade productFacade;

    @Test
    @DisplayName("해피케이스 - 정상적으로 상위 상품 5개가 판매량 기준으로 정렬되어 리턴")
    void testHappyCase() {
        List<ProductSalesResult> result = productFacade.retrieveTopProducts("TENT");

        Assertions.assertThat(result).hasSizeLessThanOrEqualTo(5);
        Assertions.assertThat(result).isSortedAccordingTo(Comparator.comparing(ProductSalesResult::salesQuantity).reversed());
    }

    @Test
    @DisplayName("주문은 있지만 OrderItem이 없는 경우 예외 발생")
    void testOrdersWithoutOrderItems() {
        Assertions.assertThatThrownBy(() -> productFacade.retrieveTopProducts("FOOD"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("해당 상품 분류가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("상품 정렬은 판매수량이 높은 순으로 정렬")
    void testSalesQuantitySorting() {
        List<ProductSalesResult> result = productFacade.retrieveTopProducts("TENT");

        Assertions.assertThat(result.size()).isGreaterThanOrEqualTo(TOP_COUNT);
    }

    @Test
    @DisplayName("결과는 최대 5개로 제한")
    void testTopCountLimit() {
        List<ProductSalesResult> result = productFacade.retrieveTopProducts("ACC");

        Assertions.assertThat(result).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    @DisplayName("유효하지 않은 카테고리 입력 시 예외 발생")
    void testInvalidCategoryThrowsException() {
        String invalidCategory = "INVALID";

        Assertions.assertThatThrownBy(() -> productFacade.retrieveTopProducts(invalidCategory))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("해당 상품 분류가 존재하지 않습니다.");
    }
}