package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.LockService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final LockService lockService;
    private final RedisTemplate<String, String> redisTemplate;

    public ProductService(
            ProductRepository productRepository
            , LockService lockService
            , @Qualifier("masterRedisTemplate") RedisTemplate<String, String> redisTemplate
    ){
        this.productRepository = productRepository;
        this.lockService = lockService;
        this.redisTemplate = redisTemplate;
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

        Product product = retrieveProduct(productId);

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

        String cacheKey = "product:" + productId;
        redisTemplate.opsForValue().set(cacheKey,product.getStock().toString());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        product.increaseStock(quantity);
        log.debug("restore stock: " + product.getStock());
        productRepository.save(product);

        String cacheKey = "product:" + productId;
        redisTemplate.opsForValue().set(cacheKey,product.getStock().toString());
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

}
