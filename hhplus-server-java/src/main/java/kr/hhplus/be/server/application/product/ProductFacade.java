package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRankingPolicy;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.*;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductRankingPolicy productRankingPolicy;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public List<ProductSalesResult> retrieveTopProducts(String category) {

        // 카테고리 null 방어
        ProductCategoryType parsedCategory = null;
        if (category != null && !category.isBlank()) {
            parsedCategory = ProductCategoryType.valueOf(category.toUpperCase());
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
                .filter(item -> productMap.containsKey(item.getProductId()))
                .collect(Collectors.groupingBy(
                        OrderItem::getProductId,
                        Collectors.summingInt(OrderItem::getQuantity)
                ));
        // 6. 정렬 및 제한 개수만큼 ProductSalesResult(상품 상세정보, 판매량) 생성
        return productToSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(TOP_COUNT)
                .map(entry -> {
                    Product product = productMap.get(entry.getKey());
                    int salesQuantity = entry.getValue();
                    return ProductSalesResult.from(product, salesQuantity);
                })
                .toList();
    }

}
