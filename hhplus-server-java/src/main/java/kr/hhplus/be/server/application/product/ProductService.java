package kr.hhplus.be.server.application.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.redis.RedisSlaveSelector;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
    @Getter
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisSlaveSelector redisSlaveSelector;
    private final ObjectMapper objectMapper;
    private final RankingRedisSortedSetService rankingService;

    public ProductService(
            ProductRepository productRepository,
            @Qualifier("masterRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            RedisSlaveSelector redisSlaveSelector, ObjectMapper objectMapper,
            RankingRedisSortedSetService rankingService){
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.redisSlaveSelector = redisSlaveSelector;
        this.objectMapper = objectMapper;
        this.rankingService = rankingService;
    }

    // 상품 목록 조회
    public List<ProductResult> retrieveAll (String category) {

        ProductCategoryType parsedCategory = null;

        if (category != null) {
            // 카테고리 유효성 체크
            parsedCategory = ProductCategoryType.valueOfIgnoreCase(category)
                    .orElseThrow(() -> new CustomException(NOT_EXIST_PRODUCT_CATEGORY));
        }

        List<Product> productList = new ArrayList<>();

        if (category == null) {
            productList = productRepository.getAll();
        }
        else {
            productList = productRepository.getByCategory(parsedCategory);
        }

        return productList.stream().map(ProductResult::from).toList();
    }

    // 상품 상세 조회
    public ProductResult retrieveDetail (Long productId) {
        RedisTemplate<String, Object> slaveRedis = redisSlaveSelector.getRandomSlave();

        String cacheKey = CacheKey.product(productId);

        // 조회
        Object cached = slaveRedis.opsForValue().get(cacheKey);
        if (cached != null && cached instanceof ProductResult) {
            return (ProductResult) cached;
        }

        Product product = retrieveProduct(productId);

        redisTemplate.opsForValue().set(
                cacheKey,
                ProductResult.from(product)
        );

        return ProductResult.from(product);
    }

    public Product retrieveProduct (Long productId) {

        return productRepository.findById(productId);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStock(Long productId, int quantity) {
            Product product = productRepository.findById(productId);
            product.decreaseStock(quantity);
            log.debug("decrease stock: " + product.getStock());
            productRepository.save(product);

        String cacheKey = CacheKey.product(productId);
        redisTemplate.opsForValue().set(
                cacheKey,
                ProductResult.from(product)
        );

        rankingService.decreaseSales(product.getCategory().name(), productId, quantity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        product.increaseStock(quantity);
        log.debug("restore stock: " + product.getStock());
        productRepository.save(product);

        String cacheKey = CacheKey.product(productId);
        redisTemplate.opsForValue().set(
                cacheKey,
                ProductResult.from(product)
        );

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
        log.info("redisKey: " + redisKey);

        String cachedJson = (String) redisTemplate.opsForValue().get(redisKey);
        log.info("cachedJson: " + cachedJson);
        if (cachedJson == null) {
            throw new IllegalStateException("스냅샷 캐시가 존재하지 않습니다.");
        }

        try {
            return objectMapper.readValue(cachedJson, new TypeReference<List<ProductSalesResult>>() {});
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 역직렬화 실패 - key: {}", redisKey, e);
            throw new RuntimeException("캐시 데이터 변환 중 오류 발생");
        }
    }

    public List<ProductRankResult> getRealTimeRank(String category) {

        Set<ZSetOperations.TypedTuple<Object>> rank = rankingService.getRank(category);

        return rank.stream()
                .map(tuple -> {
                    Long productId = convertToLong(tuple.getValue());
                    int score = tuple.getScore() == null ? 0 : tuple.getScore().intValue();
                    return new ProductRankResult(productId, score);
                })
                .toList();

    }

    private Long convertToLong(Object value) {
        if (value instanceof String str) return Long.valueOf(str);
        if (value instanceof Long l) return l;
        throw new IllegalArgumentException("Unexpected type in ZSet value: " + value.getClass());
    }

    public List<Product> getProductByCategory(ProductCategoryType category) {
        return productRepository.getByCategory(category);
    }

}
