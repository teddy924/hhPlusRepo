package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaProductRepository extends JpaRepository<Product, Long> {

    @NonNull
    List<Product> findAll();

    List<Product> findByCategory(ProductCategoryType category);

    @NonNull
    Optional<Product> findById(@NonNull Long productId);

}
