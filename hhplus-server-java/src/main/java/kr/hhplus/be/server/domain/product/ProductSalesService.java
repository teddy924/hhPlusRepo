package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSalesService {

    private final ProductSalesRepository productSalesRepository;
    private final ProductRankingPolicy productRankingPolicy;

    public List<ProductSalesDto> getTopRankProducts(String category) {

        List<ProductSalesDto> productSalesList = new ArrayList<>();

        LocalDateTime startTime = productRankingPolicy.getStartTime();  // 조회 기준 시작일자
        LocalDateTime endTime = productRankingPolicy.getEndTime();      // 조회 기준 종료일자
        int count = ProductRankingPolicy.TOP_COUNT;                     // 조회 기준 개수

        if (category == null || category.isEmpty()) {
            productSalesList = productSalesRepository.findTopRankBySales(startTime, endTime, count);
        }
        else {
            productSalesList = productSalesRepository.findTopRankBySales(startTime, endTime, count, category);
        }

        return productSalesList;

    }

}
