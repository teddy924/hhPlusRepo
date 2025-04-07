package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductSalesDto;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public Product findById(Long id) {
        return null;
    }

    @Override
    public List<Product> findByProductIds(List<ProductSalesDto> productSalesList) {
        return List.of();
    }
}
