package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();

    List<Product> findByCategory(String category);

    Optional<Product> findById(Long id);

    List<Product> findByProductIds(List<ProductSalesResult> productSalesList);

    void save(Product product);
}
