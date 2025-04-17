package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderSaveInfo;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.interfaces.order.OrderResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class OrderServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문 저장 시 주문 ID와 상태가 정상적으로 반환되어야 한다")
    void saveInitialOrder_shouldPersistOrder() {
        User user = User.builder()
                .id(10L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();
        OrderInfo info = new OrderInfo(1L, Map.of(), null,12000L);
        Order savedOrder = orderService.saveInitialOrder(user, info);

        assertNotNull(savedOrder.getId());
        assertEquals(OrderStatus.CREATED, savedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("주문 ID로 주문 상세 정보를 모두 조회할 수 있어야 한다")
    void retrieveOrderInfo_shouldReturnAllOrderDetails() {
        OrderSaveInfo info = orderService.retrieveOrderInfo(102L);

        assertNotNull(info.order());
        assertNotNull(info.orderAddress());
        assertFalse(info.orderItems().isEmpty());
        assertNotNull(info.orderCoupon());
        assertNotNull(info.orderHistory());
    }

    @Test
    @DisplayName("사용자 ID로 주문 목록을 조회할 수 있어야 한다")
    void retrieveOrdersByUserId_shouldReturnOrderList() {
        List<OrderResponseDTO> orders = orderService.retrieveOrdersByUserId(10L);

        assertFalse(orders.isEmpty());
    }
}