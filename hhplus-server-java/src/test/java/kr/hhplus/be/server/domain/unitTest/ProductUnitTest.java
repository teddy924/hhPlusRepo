package kr.hhplus.be.server.domain.unitTest;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductUnitTest {

    @Test
    @DisplayName("isExpired는 현재 시간이 유효기간 내에 있으면 true 리턴")
    void isExpired_shouldReturnTrue_whenNowInRange() {
        Product product = Product.builder()
                .efctStDt(LocalDateTime.now().minusDays(1))
                .efctFnsDt(LocalDateTime.now().plusDays(1))
                .build();

        assertTrue(product.isExpired());
    }

    @Test
    @DisplayName("isExpired는 유효기간이 아니면 false 리턴")
    void isExpired_shouldReturnFalse_whenNowOutsideRange() {
        Product product = Product.builder()
                .efctStDt(LocalDateTime.now().minusDays(10))
                .efctFnsDt(LocalDateTime.now().minusDays(5))
                .build();

        assertFalse(product.isExpired());
    }

    @Test
    @DisplayName("재고가 0 이하이면 validSalesAvailability에서 예외 발생")
    void validSalesAvailability_shouldThrow_whenOutOfStock() {
        Product product = Product.builder()
                .stock(0)
                .efctStDt(LocalDateTime.now().minusDays(1))
                .efctFnsDt(LocalDateTime.now().plusDays(1))
                .build();

        CustomException ex = assertThrows(CustomException.class, product::validSalesAvailability);
        assertTrue(ex.getMessage().contains("상품이 품절 상태 입니다."));
    }

    @Test
    @DisplayName("판매 기간이 유효하지 않으면 validSalesAvailability에서 예외 발생")
    void validSalesAvailability_shouldThrow_whenNotInSalePeriod() {
        Product product = Product.builder()
                .stock(10)
                .efctStDt(LocalDateTime.now().minusDays(10))
                .efctFnsDt(LocalDateTime.now().minusDays(1))
                .build();

        CustomException ex = assertThrows(CustomException.class, product::validSalesAvailability);
        assertTrue(ex.getMessage().contains("유효하지 않은 상품입니다."));
    }

    @Test
    @DisplayName("재고 증가 확인")
    void increaseStock_shouldIncreaseStock() {
        Product product = Product.builder().stock(5).build();

        product.increaseStock(3);

        assertEquals(8, product.getStock());
    }

    @Test
    @DisplayName("increaseStock에 0 이하를 주면 예외 발생")
    void increaseStock_shouldThrow_whenInvalidQuantity() {
        Product product = Product.builder().stock(5).build();

        CustomException ex = assertThrows(CustomException.class, () -> product.increaseStock(0));
        assertTrue(ex.getMessage().contains("유효하지 않은 상품 수량 입니다."));
    }

    @Test
    @DisplayName("재고 감소 확인")
    void decreaseStock_shouldDecreaseStock() {
        Product product = Product.builder().stock(10).build();

        product.decreaseStock(4);

        assertEquals(6, product.getStock());
    }

    @Test
    @DisplayName("decreaseStock에 음수 또는 0을 입력하면 예외 발생")
    void decreaseStock_shouldThrow_whenInvalidQuantity() {
        Product product = Product.builder().stock(10).build();

        CustomException ex = assertThrows(CustomException.class, () -> product.decreaseStock(0));
        assertTrue(ex.getMessage().contains("유효하지 않은 상품 수량 입니다."));
    }

    @Test
    @DisplayName("decreaseStock 시 재고보다 큰 수량이면 예외 발생")
    void decreaseStock_shouldThrow_whenStockNotEnough() {
        Product product = Product.builder().stock(5).build();

        CustomException ex = assertThrows(CustomException.class, () -> product.decreaseStock(10));
        assertTrue(ex.getMessage().contains("상품이 품절 상태 입니다."));
    }
}
