package kr.hhplus.be.server.application.unitTest;

import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.product.RankingRedisSortedSetService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.swagger.ErrorCode;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.domain.product.ProductCategoryType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RankingRedisSortedSetService rankingService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<Object> rBucket;

    @BeforeEach
    void setUp() {
        lenient().when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
    }

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
        when(productRepository.getAll()).thenReturn(mockProducts);

        List<ProductResult> result = productService.retrieveAll(null);

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
        when(productRepository.getByCategory(ProductCategoryType.valueOf(category))).thenReturn(mockProducts);

        List<ProductResult> result = productService.retrieveAll(category);

        assertEquals(4, result.size());
        assertEquals("10인용 텐트", result.get(0).productName());
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID 조회 시 예외 발생")
    void retrieveDetail_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(99L)).thenThrow(new CustomException(ErrorCode.NOT_EXIST_PRODUCT));

        CustomException ex = assertThrows(CustomException.class, () ->
                productService.retrieveDetail(99L));

        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("상품 재고 감소 성공")
    void decreaseStock_success() {
        Product product = new Product(1L, 321L,"10인용 텐트", 1000L, 100, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);
        when(productRepository.findById(1L)).thenReturn(product);

        productService.decreaseStock(1L, 5);

        assertEquals(95, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품 재고 감소 시 예외")
    void decreaseStock_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(999L)).thenThrow(new CustomException(ErrorCode.NOT_EXIST_PRODUCT));

        CustomException ex = assertThrows(CustomException.class, () ->
                productService.decreaseStock(999L, 5));


        assertTrue(ex.getMessage().contains("해당 상품이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("주문 상품 처리 시 모든 상품 유효성 검사 통과")
    void processOrderProducts_success() {
        Map<Long, Integer> productGrp = Map.of(1L, 2);
        Product product = mock(Product.class);

        when(productRepository.findById(1L)).thenReturn(product);

        Map<Product, Integer> result = productService.processOrderProducts(productGrp);

        assertTrue(result.containsKey(product));
        verify(product).validSalesAvailability();
    }

    @Test
    @DisplayName("캐시가 존재하지 않으면 예외가 발생한다")
    void retrieveRankSnapshot_fail_whenNoCache() {
        // given
        String category = "TENT";
        String redisKey = "snapshot:rank:TENT";

        // RedissonClient에서 getBucket 호출 시 mock RBucket 반환
        when(redissonClient.getBucket(redisKey)).thenReturn(rBucket);
        // RBucket에서 get() 호출 시 null 반환
        when(rBucket.get()).thenReturn(null);

        // expect
        assertThrows(IllegalStateException.class, () ->
                productService.retrieveRankSnapshot(category)
        );
    }
}
