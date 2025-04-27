package kr.hhplus.be.server.application.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Transactional
class ProductServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

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

        Product before = productRepository.getById(productId);
        int beforeStock = before.getStock();

        productService.decreaseStock(productId, quantity);

        Product after = productRepository.getById(productId);
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
    void restoreStock_shouldIncreaseCorrectly() {
        Long productId = 4L;
        int quantity = 5;

        Product before = productRepository.getById(productId);
        int beforeStock = before.getStock();

        productService.restoreStock(productId, quantity);

        Product after = productRepository.getById(productId);
        assertEquals(beforeStock + quantity, after.getStock());
    }

    @Test
    @DisplayName("존재하지 않는 상품 포함 시 예외 발생")
    void processOrderProducts_shouldThrow_whenInvalidProduct() {
        Map<Long, Long> orderMap = Map.of(
                1L, 2L,
                9999L, 1L
        );

        CustomException ex = assertThrows(CustomException.class, () -> productService.processOrderProducts(orderMap));
        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));

    }
}