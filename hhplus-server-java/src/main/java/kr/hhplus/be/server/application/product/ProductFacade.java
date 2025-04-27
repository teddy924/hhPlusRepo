package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRankSnapshotRepository;
import kr.hhplus.be.server.domain.product.ProductRankingPolicy;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductRankSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;
import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductRankingPolicy productRankingPolicy;
    private final ProductRepository productRepository;
    private final ProductRankSnapshotRepository productRankSnapshotRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)     // 여러 테이블을 순차적으로 조회하기 때문에 과정 도중 데이터 변경이 발생할 가능성이 있음
    public List<ProductSalesResult> retrieveRank(String category) {

        ProductCategoryType parsedCategory = null;

        if (category != null && !"ALL".equals(category)) {
            parsedCategory = ProductCategoryType.valueOfIgnoreCase(category)
                    .orElseThrow(() -> new CustomException(NOT_EXIST_PRODUCT_CATEGORY));
        }

        LocalDateTime start = productRankingPolicy.getStartTime();
        LocalDateTime end = productRankingPolicy.getEndTime();

        // 1. 일자 범위에 해당하는 주문 조회
        List<Order> orders = orderRepository.getBySysCretDtBetween(start, end);
        // 2. 해당 주문 아이디 추출
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        // 3. 주문 아이디의 상품정보 추출
        List<OrderItem> items = orderItemRepository.getByOrderIds(orderIds);
        List<Product> products = productRepository.getByCategory(parsedCategory);
        // 4. 상품아이디 별 상품 추출
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        // 5. 상품아이디 별 판매수량 추출
        Map<Long, Integer> productToSales = items.stream()
                .filter(item -> productMap.containsKey(item.getProduct().getId()))
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.summingInt(OrderItem::getQuantity)
                ));
        // 6. 정렬 및 제한 개수만큼 ProductSalesResult(상품 상세정보, 판매량) 생성
        return productToSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(TOP_COUNT)
                .map(entry -> {
                    Product product = productMap.get(entry.getKey());
                    int salesQuantity = entry.getValue();
                    return ProductSalesResult.from(product, salesQuantity, category);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductSalesResult> retrieveRankSnapshot(String category) {
        log.debug("retrieveRankSnapshot category: {}", category);

        String categoryKey = (category == null) ? "ALL" : category.toUpperCase();

        log.debug("categoryKey: {}", categoryKey);

        LocalDateTime lastSnapshot = productRankSnapshotRepository.getLatestSnapshotAt(categoryKey);

        log.debug("last snapshot: {}", lastSnapshot);

        List<ProductRankSnapshot> snapshots = productRankSnapshotRepository.getByCategoryAndSnapshotAtOrderByRankingAsc(categoryKey, lastSnapshot);

        for (ProductRankSnapshot snapshot : snapshots) {
            log.debug("snapshot productId {}", snapshot.getProductId());
        }

        return snapshots.stream()
                .map(ProductSalesResult::from)
                .toList();
    }


}
