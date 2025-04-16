package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.NOT_EXIST_PRODUCT;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public List<Product> getAll() {
        return jpaProductRepository.findAll();
    }

    @Override
    public List<Product> getByCategory(ProductCategoryType category) {
        if (category == null) {
            return jpaProductRepository.findAll();
        }
        return jpaProductRepository.findByCategory(category);
    }

    @Override
    public Product getById(Long productId) {
        return jpaProductRepository.findById(productId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_PRODUCT));
    }

    @Override
    public void save(Product product) {
        jpaProductRepository.save(product);
    }
}
