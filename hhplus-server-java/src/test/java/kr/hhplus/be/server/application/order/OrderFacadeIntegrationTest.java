package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderAddressInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class OrderFacadeIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OrderFacade orderFacade;

    @Test
    @DisplayName("주문 실패 - 재고 부족")
    void orderFails_whenStockIsInsufficient() {
        OrderCommand command = OrderCommand.builder()
                .userId(80001L)
                .productGrp(Map.of(
                        90001L, 200L
                ))
                .couponId(null)
                .orderAddressInfo(address())
                .build();
        CustomException exception = assertThrows(CustomException.class, () -> orderFacade.order(command));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("상품이 품절 상태 입니다."));
    }

    @Test
    @DisplayName("주문 실패 - 쿠폰 조회 실패")
    void orderFails_whenCouponNotFound() {
        OrderCommand command = OrderCommand.builder()
                .userId(2L)
                .productGrp(Map.of(
                        10L, 10L,
                        11L, 20L,
                        12L,30L
                ))
                .couponId(9999L)
                .orderAddressInfo(address())
                .build();
        CustomException exception = assertThrows(CustomException.class, () -> orderFacade.order(command));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("주문 실패 - 쿠폰 발급 이력 없음")
    void orderFails_whenCouponIssueNotFound() {
        OrderCommand command = OrderCommand.builder()
                .userId(1L)
                .productGrp(Map.of(
                        10L, 10L,
                        11L, 20L,
                        12L,30L
                ))
                .couponId(3L)
                .orderAddressInfo(address())
                .build();
        CustomException exception = assertThrows(CustomException.class, () -> orderFacade.order(command));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("보유하고 있는 쿠폰이 없습니다."));
    }

    @Test
    @DisplayName("주문 실패 - 잔액 부족으로 결제 실패")
    void orderFails_whenBalanceIsInsufficient() {
        OrderCommand command = OrderCommand.builder()
                .userId(2L)
                .productGrp(Map.of(
                        10L, 10L,
                        11L, 20L,
                        12L,30L
                ))
                .couponId(null)
                .orderAddressInfo(address())
                .build();
        CustomException exception = assertThrows(CustomException.class, () -> orderFacade.order(command));
        assertTrue(exception.getMessage().contains("잔액이 부족합니다"));
    }

    @Test
    @DisplayName("주문 실패 - 쿠폰 사용 실패")
    void orderFails_whenCouponUsageFails() {
        OrderCommand command = OrderCommand.builder()
                .userId(80001L)
                .productGrp(Map.of(
                        10L, 10L,
                        11L, 20L,
                        12L,30L
                ))
                .couponId(4L)
                .orderAddressInfo(address())
                .build();
        CustomException exception = assertThrows(CustomException.class, () -> orderFacade.order(command));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("쿠폰 사용에 실패하였습니다."));
    }

    @Test
    @DisplayName("주문 취소 실패 - 주문 이력 조회 실패")
    void cancelFails_whenOrderHistoryMissing() {
        OrderCancelCommand command = new OrderCancelCommand(99999L);
        CustomException exception = assertThrows(CustomException.class, () -> orderFacade.cancel(command));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("주문 정보를 찾을 수 없습니다"));
    }

    private OrderAddressInfo address() {
        return OrderAddressInfo.builder()
                .orderId(2L)
                .receiverName("테스트 수령인")
                .phone("01011112222")
                .address1("과제시 지옥구 살려동")
                .address2("1101동 101호")
                .zipcode("12345")
                .sysCretDt(LocalDateTime.now())
                .build();
    }
}