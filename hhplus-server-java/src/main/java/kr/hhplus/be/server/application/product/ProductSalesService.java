package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductRankingPolicy;
import kr.hhplus.be.server.domain.product.ProductSalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSalesService {

    private final ProductSalesRepository productSalesRepository;
    private final ProductRankingPolicy productRankingPolicy;

    public List<ProductSalesResult> getTopRankProducts(String category) {

        List<ProductSalesResult> productSalesList = new ArrayList<>();

        LocalDateTime startTime = productRankingPolicy.getStartTime();  // 조회 기준 시작일자
        LocalDateTime endTime = productRankingPolicy.getEndTime();      // 조회 기준 종료일자
        int count = ProductRankingPolicy.TOP_COUNT;                     // 조회 기준 개수

        if (category == null || category.isEmpty()) {
            productSalesList = productSalesRepository.findTopRankBySales(startTime, endTime, count);
        }
        else {
            productSalesList = productSalesRepository.findTopRankBySales(startTime, endTime, count, category);
        }

        productSalesList.sort(Comparator.comparing(ProductSalesResult::salesQuantity).reversed());

        return productSalesList;

    }

}
