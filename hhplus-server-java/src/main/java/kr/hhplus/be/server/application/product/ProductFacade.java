package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.order.OrderService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.ProductRankingPolicy;
import kr.hhplus.be.server.domain.product.entity.Product;
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
//    private final ProductRepository productRepository;
//    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ProductService productService;

    @Transactional(readOnly = true)     // 여러 테이블을 순차적으로 조회하기 때문에 과정 도중 데이터 변경이 발생할 가능성이 있음
    public List<ProductSalesResult> retrieveRank(String category) {

        ProductCategoryType parsedCategory = null;

        if (category != null && !"ALL".equals(category)) {
            parsedCategory = ProductCategoryType.valueOfIgnoreCase(category)
                    .orElseThrow(() -> new CustomException(NOT_EXIST_PRODUCT_CATEGORY));
        }

        LocalDateTime start = productRankingPolicy.getStartTime();
        LocalDateTime end = productRankingPolicy.getEndTime();

        // @todo 각 도메인의 service에서 구현 후 조합하는게 좋다.
//        // 1. 일자 범위에 해당하는 주문 조회
//        List<Order> orders = orderRepository.getBySysCretDtBetween(start, end);
//        // 2. 해당 주문 아이디 추출
//        List<Long> orderIds = orders.stream().map(Order::getId).toList();
//        // 3. 주문 아이디의 상품정보 추출
//        List<OrderItem> items = orderItemRepository.getByOrderIds(orderIds);
//        List<Product> products = productRepository.getByCategory(parsedCategory);
//        // 4. 상품아이디 별 상품 추출
//        Map<Long, Product> productMap = products.stream()
//                .collect(Collectors.toMap(Product::getId, Function.identity()));
//        // 5. 상품아이디 별 판매수량 추출
//        Map<Long, Integer> productToSales = items.stream()
//                .filter(item -> productMap.containsKey(item.getProduct().getId()))
//                .collect(Collectors.groupingBy(
//                        item -> item.getProduct().getId(),
//                        Collectors.summingInt(OrderItem::getQuantity)
//                ));
//        // 6. 정렬 및 제한 개수만큼 ProductSalesResult(상품 상세정보, 판매량) 생성
//        return productToSales.entrySet().stream()
//                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
//                .limit(TOP_COUNT)
//                .map(entry -> {
//                    Product product = productMap.get(entry.getKey());
//                    int salesQuantity = entry.getValue();
//                    return ProductSalesResult.from(product, salesQuantity, category);
//                })
//                .toList();

        // 🌟 refactoring - 책임 분리 - 각 도메인 서비스로 로직 이동
        List<OrderItem> orderItemList = orderService.getOrderItemsBetween(start, end);
        List<Product> productList = productService.getProductByCategory(parsedCategory);

        return aggregate(orderItemList, productList, category);

    }

    private List<ProductSalesResult> aggregate(List<OrderItem> orderItemList, List<Product> productList, String category ) {
         // 상품아이디 별 상품 추출
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        // 상품아이디 별 판매수량 추출
        Map<Long, Integer> productToSales = orderItemList.stream()
                .filter(item -> productMap.containsKey(item.getProduct().getId()))
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.summingInt(OrderItem::getQuantity)
                ));
        // 정렬 및 제한 개수만큼 ProductSalesResult(상품 상세정보, 판매량) 생성
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

}
