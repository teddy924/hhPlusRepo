package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록 조회
    public List<ProductResult> retrieveAll (String category) {

        ProductCategoryType categoryType = null;

        if (category != null && !category.isEmpty()) {
            categoryType = ProductCategoryType.valueOf(category.toUpperCase());
        }

        List<Product> productList = new ArrayList<>();

        if (category == null) {
            productList = productRepository.getAll();
        }
        else {
            productList = productRepository.getByCategory(categoryType);
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

        product.decreaseStock(quantity); // 도메인 로직 위임
        productRepository.save(product);
    }

    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.getById(productId);

        product.increaseStock(quantity); // domain method
        productRepository.save(product);
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
