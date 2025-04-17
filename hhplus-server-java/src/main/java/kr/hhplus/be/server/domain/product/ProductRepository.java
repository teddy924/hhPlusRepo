package kr.hhplus.be.server.domain.product;

import jakarta.annotation.Nullable;
import kr.hhplus.be.server.domain.product.entity.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> getAll();

    List<Product> getByCategory(@Nullable ProductCategoryType category);

    Product getById(Long id);

    void save(Product product);
}
