package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.interfaces.order.OrderResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.domain.product.ProductCategoryType.ACC;
import static kr.hhplus.be.server.domain.product.ProductCategoryType.TENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock private OrderAddressRepository orderAddressRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderCouponRepository orderCouponRepository;
    @Mock private OrderHistoryRepository orderHistoryRepository;

    @Test
    @DisplayName("상품 목록의 가격 * 수량 총합을 계산")
    void calculateTotalAmount_success() {
        Product p1 = new Product(1L, 321L,"10인용 텐트", 5000L, null, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);
        Product p2 = new Product(3L, 321L,"초거대 랜턴", 3000L, null, ACC, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);

        Map<Product, Long> orderMap = Map.of(p1, 2L, p2, 3L);

        Long result = orderService.calculateTotalAmount(orderMap);

        assertEquals(19000L, result);
    }

    @Test
    @DisplayName("주문 초기 저장 성공 시 ID로 조회된 주문이 반환")
    void saveInitialOrder_success() {
        OrderInfo info = OrderInfo.builder().userId(1L).totPrice(5000L).build();
        Order order = Order.builder().id(10L).userId(1L).totalAmount(5000L).build();

        when(orderRepository.saveAndReturnId(any())).thenReturn(10L);
        when(orderRepository.getById(10L)).thenReturn(order);

        Order result = orderService.saveInitialOrder(info);

        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("상품 목록을 OrderItem 목록으로 변환")
    void buildOrderItemList_success() {
        Order order = Order.builder().id(1L).build();
        Product p1 = new Product(1L, 321L,"10인용 텐트", 5000L, null, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);
        Product p2 = new Product(3L, 321L,"초거대 랜턴", 3000L, null, ACC, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);

        Map<Product, Long> orderMap = Map.of(p1, 2L, p2, 3L);

        List<OrderItem> result = orderService.buildOrderItemList(order, orderMap);

        assertEquals(2, result.size());
        assertEquals(10000L, result.get(0).getTotAmount());
    }

    @Test
    @DisplayName("쿠폰 할인 금액 계산 및 OrderCoupon 생성")
    void buildOrderCoupon_success() {
        Order order = Order.builder().id(1L).build();
        CouponIssue issue = new CouponIssue(1L, 1L, 10L, CouponStatus.ISSUED, LocalDateTime.now(), null);
        Coupon coupon = new Coupon(10L, "할인", CouponDiscountType.AMOUNT, 1000L, 3000, 1500,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10), null, null);
        CouponInfo couponInfo = new CouponInfo(coupon, issue);

        OrderCoupon result = orderService.buildOrderCoupon(couponInfo, order, 5000L);

        assertEquals(1000L, result.getDiscountAmount());
    }

    @Test
    @DisplayName("주문 관련 정보들을 저장")
    void saveOrderRelated_shouldSaveAll() {
        Order order = mock(Order.class);
        OrderAddress addr = mock(OrderAddress.class);
        OrderHistory hist = mock(OrderHistory.class);
        OrderCoupon coupon = mock(OrderCoupon.class);
        OrderItem item = mock(OrderItem.class);

        OrderSaveInfo saveInfo = OrderSaveInfo.builder()
                .order(order)
                .orderAddress(addr)
                .orderItems(List.of(item))
                .orderCoupon(coupon)
                .orderHistory(hist)
                .build();

        orderService.saveOrderRelated(saveInfo);

        verify(orderAddressRepository).save(addr);
        verify(orderItemRepository).save(List.of(item));
        verify(orderCouponRepository).save(coupon);
        verify(orderHistoryRepository).save(hist);
    }

    @Test
    @DisplayName("주문 ID로 모든 주문 관련 정보들을 조회")
    void retrieveOrderInfo_success() {
        Long orderId = 1L;

        Order order = Order.builder().id(orderId).build();
        OrderAddress addr = new OrderAddress(1L,10L,"우수리", "01011112222", "침대시", "자구싶동", "12345", null, LocalDateTime.now(), null);
        OrderHistory hist = new OrderHistory(1L, 10L, OrderHistoryStatus.PAID, null, LocalDateTime.now());
        List<OrderItem> items = List.of(new OrderItem(1L, 10L, 14L, 3, 30000L, LocalDateTime.now(), null));
        OrderCoupon coupon = new OrderCoupon(1L, 10L, 22L, 1500L, LocalDateTime.now(), LocalDateTime.now(), null);

        when(orderRepository.getById(orderId)).thenReturn(order);
        when(orderAddressRepository.getByOrderId(orderId)).thenReturn(addr);
        when(orderHistoryRepository.getByOrderId(orderId)).thenReturn(hist);
        when(orderItemRepository.getByOrderId(orderId)).thenReturn(items);
        when(orderCouponRepository.getByOrderId(orderId)).thenReturn(coupon);

        OrderSaveInfo result = orderService.retrieveOrderInfo(orderId);

        assertEquals(order, result.order());
        assertEquals(1, result.orderItems().size());
    }

    @Test
    @DisplayName("유저 ID로 주문 목록을 조회")
    void retrieveOrdersByUserId_success() {
        Order order = Order.builder().id(1L).userId(10L).totalAmount(5000L).build();
        when(orderRepository.getAllByUserId(10L)).thenReturn(List.of(order));

        List<OrderResponseDTO> result = orderService.retrieveOrdersByUserId(10L);

        assertEquals(1, result.size());
        assertEquals(5000L, result.get(0).totalAmount());
    }
}
