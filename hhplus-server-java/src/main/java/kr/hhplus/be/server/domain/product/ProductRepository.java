package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository {
    List<Product> findAll();

    List<Product> findByCategory(String category);

    Product findById(Long id);

    List<Product> findByProductIds(List<ProductSalesDto> productSalesList);
}
