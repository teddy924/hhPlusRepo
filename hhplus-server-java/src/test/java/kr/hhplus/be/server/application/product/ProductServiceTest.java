package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.interfaces.product.ProductResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.hhplus.be.server.domain.product.ProductCategoryType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSalesService productSalesService;

    @Test
    @DisplayName("카테고리 없이 전체 상품 조회 성공")
    void retrieveAll_withoutCategory() {
        List<Product> mockProducts = List.of(
                new Product(1L, 321L,"10인용 텐트", 1000L, 100, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(2L, 322L,"12인용 침대", 2000L, 130, FURNITURE, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(3L, 321L,"초거대 랜턴", 3000L, 160, ACC, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(4L, 323L,"16인용 텐트", 4000L, 190, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(5L, 321L,"18인용 텐트", 5000L, 220, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(6L, 324L,"20인용 텐트", 6000L, 250, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null)
        );
        when(productRepository.findAll()).thenReturn(mockProducts);

        List<ProductResponseDTO> result = productService.retrieveAll(null);

        assertEquals(6, result.size());
    }

    @Test
    @DisplayName("카테고리 조건으로 상품 조회 성공")
    void retrieveAll_withCategory() {
        String category = "TENT";
        List<Product> mockProducts = List.of(
                new Product(1L, 321L,"10인용 텐트", 1000L, 100, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(4L, 323L,"16인용 텐트", 4000L, 190, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(5L, 321L,"18인용 텐트", 5000L, 220, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(6L, 324L,"20인용 텐트", 6000L, 250, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null)
        );
        when(productRepository.findByCategory(category)).thenReturn(mockProducts);

        List<ProductResponseDTO> result = productService.retrieveAll(category);

        assertEquals(4, result.size());
        assertEquals("10인용 텐트", result.get(0).getProductName());
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID 조회 시 예외 발생")
    void retrieveDetail_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                productService.retrieveDetail(99L));

        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("상위 상품 조회 시 판매량 기준 상품 반환")
    void retrieveTopRank_success() {
        List<ProductSalesResult> sales = List.of(new ProductSalesResult(1L, 300L));
        List<Product> mockProducts = List.of(
                new Product(1L, 321L,"10인용 텐트", 1000L, 100, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(2L, 322L,"12인용 침대", 2000L, 130, FURNITURE, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(3L, 321L,"초거대 랜턴", 3000L, 160, ACC, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(4L, 323L,"16인용 텐트", 4000L, 190, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(5L, 321L,"18인용 텐트", 5000L, 220, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null),
                new Product(6L, 324L,"20인용 텐트", 6000L, 250, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null)
        );

        when(productSalesService.getTopRankProducts(null)).thenReturn(sales);
        when(productRepository.findByProductIds(sales)).thenReturn(mockProducts);

        List<ProductResponseDTO> result = productService.retrieveTopRank(null);

        assertEquals("10인용 텐트", result.get(0).getProductName());
    }

    @Test
    @DisplayName("상품 재고 감소 성공")
    void decreaseStock_success() {
        Product product = new Product(1L, 321L,"10인용 텐트", 1000L, 100, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.decreaseStock(1L, 5);

        assertEquals(95, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품 재고 감소 시 예외")
    void decreaseStock_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () ->
                productService.decreaseStock(999L, 5));

        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("주문 상품 처리 시 모든 상품 유효성 검사 통과")
    void processOrderProducts_success() {
        Map<Long, Long> productGrp = Map.of(1L, 2L);
        Product product = mock(Product.class);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Map<Product, Long> result = productService.processOrderProducts(productGrp);

        assertTrue(result.containsKey(product));
        verify(product).validSalesAvailability();
    }
}
