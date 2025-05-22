package kr.hhplus.be.server.application.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final RankingRedisSortedSetService rankingService;
    private final RedissonClient redissonClient;

    public ProductService(
            ProductRepository productRepository,
            ObjectMapper objectMapper,
            RankingRedisSortedSetService rankingService,
            RedissonClient redissonClient) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
        this.rankingService = rankingService;
        this.redissonClient = redissonClient;
    }

    // 상품 목록 조회
    public List<ProductResult> retrieveAll(String category) {
        ProductCategoryType parsedCategory = null;
        if (category != null) {
            parsedCategory = ProductCategoryType.valueOfIgnoreCase(category)
                    .orElseThrow(() -> new CustomException(NOT_EXIST_PRODUCT_CATEGORY));
        }

        List<Product> productList = (parsedCategory == null)
                ? productRepository.getAll()
                : productRepository.getByCategory(parsedCategory);

        return productList.stream().map(ProductResult::from).toList();
    }

    // 상품 상세 조회
    public ProductResult retrieveDetail(Long productId) {
        String cacheKey = CacheKey.product(productId);
        RBucket<ProductResult> bucket = redissonClient.getBucket(cacheKey);

        ProductResult cached = bucket.get();
        if (cached != null) {
            return cached;
        }

        Product product = retrieveProduct(productId);
        ProductResult result = ProductResult.from(product);
        bucket.set(result);

        return result;
    }

    public Product retrieveProduct(Long productId) {
        return productRepository.findById(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        product.decreaseStock(quantity);
        log.debug("decrease stock: {}", product.getStock());
        productRepository.save(product);

        redissonClient.getBucket(CacheKey.product(productId))
                .set(ProductResult.from(product));

        rankingService.decreaseSales(product.getCategory().name(), productId, quantity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        product.increaseStock(quantity);
        log.debug("restore stock: {}", product.getStock());
        productRepository.save(product);

        redissonClient.getBucket(CacheKey.product(productId))
                .set(ProductResult.from(product));

        rankingService.increaseSales(product.getCategory().name(), productId, quantity);
    }

    public Map<Product, Integer> processOrderProducts(Map<Long, Integer> productGrp) {
        Map<Product, Integer> result = productGrp.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> retrieveProduct(entry.getKey()),
                        Map.Entry::getValue
                ));

        result.keySet().forEach(Product::validSalesAvailability);

        return result;
    }

    @Transactional(readOnly = true)
    public List<ProductSalesResult> retrieveRankSnapshot(String category) {
        String categoryKey = (category == null) ? "ALL" : category.toUpperCase();
        String redisKey = CacheKey.ranking(categoryKey);
        log.info("redisKey: {}", redisKey);

        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        String cachedJson = bucket.get();
        log.info("cachedJson: {}", cachedJson);

        if (cachedJson == null) {
            throw new IllegalStateException("스냅샷 캐시가 존재하지 않습니다.");
        }

        try {
            return objectMapper.readValue(cachedJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 역직렬화 실패 - key: {}", redisKey, e);
            throw new RuntimeException("캐시 데이터 변환 중 오류 발생");
        }
    }

    public List<ProductRankResult> getRealTimeRank(String category) {
        Set<ScoredEntry<Long>> rank = rankingService.getRank(category);

        return rank.stream()
                .map(entry -> new ProductRankResult(entry.getValue(), entry.getScore().intValue()))
                .toList();
    }

    public List<Product> getProductByCategory(ProductCategoryType category) {
        return productRepository.getByCategory(category);
    }
}
