package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductSalesDto;
import kr.hhplus.be.server.domain.product.ProductSalesService;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductSalesService productSalesService;
    private final ProductRepository productRepository;

    // 상품 목록 조회
    public List<ProductQueryDto> retrieveAll (String category) {

        List<Product> productList = new ArrayList<>();

        if (category == null || category.isEmpty()) {
            productList = productRepository.findAll();
        }
        else {
            productList = productRepository.findByCategory(category);
        }

        return productList.stream().map(ProductQueryDto::from).toList();
    }

    // 상품 상세 조회
    public ProductQueryDto retrieveDetail (Long id) {

        Product product = productRepository.findById(id);

        return ProductQueryDto.from(product);

    }

    // 상위 top5 상품 조회
    public List<ProductQueryDto> retrieveTopRank (String category) {

        List<Product> productList = new ArrayList<>();
        List<ProductSalesDto> productSalesList = new ArrayList<>();

        productSalesList = productSalesService.getTopRankProducts(category);

        productList = productRepository.findByProductIds(productSalesList);

        return productList.stream().map(ProductQueryDto::from).toList();

    }

}
