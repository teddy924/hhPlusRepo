package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.interfaces.product.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductSalesService productSalesService;
    private final ProductRepository productRepository;

    // 상품 목록 조회
    public List<ProductResponseDTO> retrieveAll (String category) {

        List<Product> productList = new ArrayList<>();

        if (category == null || category.isEmpty()) {
            productList = productRepository.findAll();
        }
        else {
            productList = productRepository.findByCategory(category);
        }

        List<ProductResult> resultList = productList.stream().map(ProductResult::from).toList();

        return resultList.stream().map(ProductResponseDTO::from).toList();
    }

    // 상품 상세 조회
    public ProductResponseDTO retrieveDetail (Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_PRODUCT));

        return ProductResponseDTO.from(ProductResult.from(product));

    }

    // 상위 top5 상품 조회
    public List<ProductResponseDTO> retrieveTopRank (String category) {

        List<ProductSalesResult> productSalesList = productSalesService.getTopRankProducts(category);

        List<Product> productList = productRepository.findByProductIds(productSalesList);

        List<ProductResult> resultList = productList.stream().map(ProductResult::from).toList();

        return resultList.stream().map(ProductResponseDTO::from).toList();

    }

}
