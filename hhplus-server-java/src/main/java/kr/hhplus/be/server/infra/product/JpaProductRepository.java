package kr.hhplus.be.server.infra.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaProductRepository extends JpaRepository<Product, Long> {

    @NonNull
    List<Product> findAll();

    List<Product> findByCategory(ProductCategoryType category);

    @NonNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Product a where a.id = :productId")
    Optional<Product> findByIdWithLock(@NonNull @Param("productId") Long productId);

    @NonNull
    Optional<Product> findById(@NonNull Long productId);

}
