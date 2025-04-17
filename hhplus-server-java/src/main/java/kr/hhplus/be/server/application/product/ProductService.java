package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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

        return productRepository.getById(productId);

    }

    public void decreaseStock(Long productId, int quantity) {
            Product product = productRepository.getById(productId);
            product.decreaseStock(quantity);
            productRepository.save(product);
    }

    public void restoreStock(Long productId, int quantity) {
        try {
            Product product = productRepository.getById(productId);
            product.increaseStock(quantity);
            productRepository.save(product);
        } catch (Exception e) {
            throw new CustomException(FAIL_RESTORE_STOCK);
        }
    }

    public Map<Product, Long> processOrderProducts(Map<Long, Long> productGrp) {
        Map<Product, Long> result = productGrp.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> retrieveProduct(entry.getKey()),
                        Map.Entry::getValue
                ));

        result.keySet().forEach(Product::validSalesAvailability);

        return result;
    }

}
