package kr.hhplus.be.server.application.unitTest;

import kr.hhplus.be.server.application.order.OrderService;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.entity.*;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.user.entity.User;
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

        Map<Product, Integer> orderMap = Map.of(p1, 2, p2, 3);

        Long result = orderService.calculateTotalAmount(orderMap);

        assertEquals(19000L, result);
    }

    @Test
    @DisplayName("주문 초기 저장 성공 시 ID로 조회된 주문이 반환")
    void saveInitialOrder_success() {
        // given
        User user = User.builder().id(1L).name("테스트 유저").build();
        OrderInfo info = OrderInfo.builder()
                .userId(user.getId())
                .totPrice(5000L)
                .build();

        Order expectedOrder = Order.builder()
                .id(10L)
                .user(user)
                .totalAmount(5000L)
                .orderStatus(OrderStatus.CREATED)
                .build();

        when(orderRepository.saveAndReturnId(any())).thenReturn(10L);
        when(orderRepository.getById(10L)).thenReturn(expectedOrder);

        // when
        Order result = orderService.saveInitialOrder(user, info);

        // then
        assertEquals(10L, result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(5000L, result.getTotalAmount());
    }

    @Test
    @DisplayName("상품 목록을 OrderItem 목록으로 변환")
    void buildOrderItemList_success() {
        Order order = Order.builder().id(1L).build();
        Product p1 = new Product(1L, 321L,"10인용 텐트", 5000L, null, TENT, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);
        Product p2 = new Product(3L, 321L,"초거대 랜턴", 3000L, null, ACC, LocalDateTime.now().minusDays(10L), LocalDateTime.now().plusDays(30L), LocalDateTime.now().minusDays(10L), null);

        Map<Product, Integer> orderMap = Map.of(p1, 2, p2, 3);

        List<OrderItem> result = orderService.buildOrderItemList(order, orderMap);

        assertEquals(2, result.size());
        assertEquals(10000L, result.get(0).getTotalAmount());
    }

    @Test
    @DisplayName("쿠폰 할인 금액 계산 및 OrderCoupon 생성")
    void buildOrderCoupon_success() {
        User user = User.builder().id(1L).name("테스터").build();
        Coupon coupon = Coupon.builder()
                .id(10L)
                .name("할인")
                .discountType(CouponDiscountType.AMOUNT)
                .discountValue(1000L)
                .limitQuantity(3000)
                .remainQuantity(1500)
                .efctStDt(LocalDateTime.now().minusDays(10))
                .efctFnsDt(LocalDateTime.now().plusDays(10))
                .build();

        CouponIssue issue = CouponIssue.builder()
                .id(1L)
                .user(user)
                .coupon(coupon)
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .build();

        Order order = Order.builder().id(1L).build();
        CouponInfo couponInfo = new CouponInfo(coupon, issue);

        OrderCoupon result = orderService.buildOrderCoupon(couponInfo, order, 5000L);

        assertEquals(1000L, result.getDiscountAmount());
        assertEquals(order.getId(), result.getOrder().getId());
        assertEquals(issue.getId(), result.getCouponIssueId());
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

        OrderAddress addr = OrderAddress.builder()
                .id(1L)
                .order(order)
                .receiverName("우수리")
                .phone("01011112222")
                .address1("침대시")
                .address2("자구싶동")
                .zipcode("12345")
                .sysCretDt(LocalDateTime.now())
                .build();

        OrderHistory hist = OrderHistory.builder()
                .id(1L)
                .order(order)
                .status(OrderHistoryStatus.PAID)
                .sysCretDt(LocalDateTime.now())
                .build();

        Product product = Product.builder().id(14L).name("매트리스").price(10000L).build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(3)
                .totalAmount(30000L)
                .sysCretDt(LocalDateTime.now())
                .build();

        CouponIssue couponIssue = CouponIssue.builder()
                .id(22L)
                .build();

        OrderCoupon coupon = OrderCoupon.builder()
                .id(1L)
                .order(order)
                .couponIssueId(couponIssue.getId())
                .discountAmount(1500L)
                .usedDt(LocalDateTime.now())
                .sysCretDt(LocalDateTime.now())
                .build();

        when(orderRepository.getById(orderId)).thenReturn(order);
        when(orderAddressRepository.getByOrderId(orderId)).thenReturn(addr);
        when(orderHistoryRepository.getByOrderId(orderId)).thenReturn(hist);
        when(orderItemRepository.getByOrderId(orderId)).thenReturn(List.of(item));
        when(orderCouponRepository.getByOrderId(orderId)).thenReturn(coupon);

        OrderSaveInfo result = orderService.retrieveOrderInfo(orderId);

        assertEquals(order, result.order());
        assertEquals(1, result.orderItems().size());
    }

    @Test
    @DisplayName("유저 ID로 주문 목록을 조회")
    void retrieveOrdersByUserId_success() {
        User user = User.builder()
                .id(10L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .totalAmount(5000L)
                .orderStatus(OrderStatus.CREATED)
                .build();

        when(orderRepository.getAllByUserId(10L)).thenReturn(List.of(order));

        List<OrderResponseDTO> result = orderService.retrieveOrdersByUserId(10L);

        assertEquals(1, result.size());
        assertEquals(5000L, result.get(0).totalAmount());
        assertEquals(OrderStatus.CREATED, result.get(0).orderStatus());
    }
}
