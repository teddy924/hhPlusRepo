package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public List<Product> findAll() {
        return List.of();
    }

    @Override
    public List<Product> findByCategory(String category) {
        return List.of();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Product> findByProductIds(List<ProductSalesResult> productSalesList) {
        return List.of();
    }

    @Override
    public void save(Product product) {

    }
}
