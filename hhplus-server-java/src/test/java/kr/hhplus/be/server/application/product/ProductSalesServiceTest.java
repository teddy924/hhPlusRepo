package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductRankingPolicy;
import kr.hhplus.be.server.domain.product.ProductSalesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSalesServiceTest {

    @InjectMocks
    private ProductSalesService productSalesService;

    @Mock
    private ProductSalesRepository productSalesRepository;

    @Mock
    private ProductRankingPolicy productRankingPolicy;

    @Test
    @DisplayName("카테고리가 없는 경우 전체 상위 상품을 조회한다")
    void getTopRankProducts_withoutCategory() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();
        int count = 5;

        List<ProductSalesResult> mockResults = Arrays.asList(
                new ProductSalesResult(1L, 300L),
                new ProductSalesResult(2L, 250L),
                new ProductSalesResult(3L, 550L),
                new ProductSalesResult(4L, 750L),
                new ProductSalesResult(5L, 150L),
                new ProductSalesResult(6L, 950L),
                new ProductSalesResult(7L, 450L)
        );

        when(productRankingPolicy.getStartTime()).thenReturn(start);
        when(productRankingPolicy.getEndTime()).thenReturn(end);
        when(productSalesRepository.findTopRankBySales(start, end, count)).thenReturn(mockResults);

        // when
        List<ProductSalesResult> result = productSalesService.getTopRankProducts(null);

        // then
        assertEquals(6L, result.get(0).productId());
        verify(productSalesRepository).findTopRankBySales(start, end, count);
    }

    @Test
    @DisplayName("카테고리가 있는 경우 해당 카테고리의 상위 상품을 조회한다")
    void getTopRankProducts_withCategory() {
        // given
        String category = "TENT";
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();
        int count = 5;

        List<ProductSalesResult> mockResults = Arrays.asList(
                new ProductSalesResult(10L, 500L),
                new ProductSalesResult(11L, 400L),
                new ProductSalesResult(12L, 300L),
                new ProductSalesResult(13L, 600L),
                new ProductSalesResult(14L, 700L),
                new ProductSalesResult(15L, 800L)
        );

        when(productRankingPolicy.getStartTime()).thenReturn(start);
        when(productRankingPolicy.getEndTime()).thenReturn(end);
        when(productSalesRepository.findTopRankBySales(start, end, count, category)).thenReturn(mockResults);

        // when
        List<ProductSalesResult> result = productSalesService.getTopRankProducts(category);

        // then
        assertEquals(15L, result.get(0).productId());
        verify(productSalesRepository).findTopRankBySales(start, end, count, category);
    }
}
