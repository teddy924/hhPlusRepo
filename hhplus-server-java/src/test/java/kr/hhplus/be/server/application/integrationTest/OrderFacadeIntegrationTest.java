package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.order.OrderCancelCommand;
import kr.hhplus.be.server.application.order.OrderCommand;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderAddressInfo;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class OrderFacadeIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(OrderFacadeIntegrationTest.class);
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("주문 실패 - 재고 부족")
    void orderFails_whenStockIsInsufficient() {
        OrderCommand command = OrderCommand.builder()
                .userId(800001L)
                .productGrp(Map.of(
                        900001L, 200
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
                        10L, 10,
                        11L, 20,
                        12L,30
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
                        10L, 10,
                        11L, 20,
                        12L,30
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
                        10L, 10,
                        11L, 20,
                        12L,30
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
                .userId(800001L)
                .productGrp(Map.of(
                        10L, 10,
                        11L, 20,
                        12L,30
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

    @Test
    @DisplayName("동일 상품 주문 두 건 동시 취소시 재고 복구 동시성 테스트")
    void concurrentOrderCancel_shouldRestoreStockExactlyOncePerOrder() throws Exception {
        Long productId = 900003L;
        Long orderIdA = 600003L;        // 각 주문은 수량을 1개씩으로 세팅해둠
        Long orderIdB = 600004L;
        int orderQuantity = 1;

        Product productBefore = productRepository.findById(productId);
        int stockBefore = productBefore.getStock();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Runnable cancelTaskA = () -> {
            try {
                startLatch.await();
                orderFacade.cancel(new OrderCancelCommand(orderIdA));
            } catch (Exception e) {
                log.warn("Order A cancel exception: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable cancelTaskB = () -> {
            try {
                startLatch.await();
                orderFacade.cancel(new OrderCancelCommand(orderIdB));
            } catch (Exception e) {
                log.warn("Order B cancel exception: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(cancelTaskA);
        executor.submit(cancelTaskB);

        // 동시에 시작!
        startLatch.countDown();
        // 둘 다 끝날 때까지 대기
        doneLatch.await();

        executor.shutdown();

        // --- 검증 ---
        Product productAfter = productRepository.findById(productId);
        int stockAfter = productAfter.getStock();

        log.debug("stockBefore: " + stockBefore);
        log.debug("stockAfter: " + stockAfter);

        // 취소 2건이 각각 1개씩만 복구되어야 함 (중복/누락 없이)
        assertEquals(stockBefore + (2 * orderQuantity), stockAfter,
                "동시 주문 취소시 재고가 2건만큼 정확히 복구되어야 함");
    }

    @Test
    @DisplayName("동일 상품 주문 두 건 동시 주문시 재고 복구 동시성 테스트")
    void concurrentOrder_shouldDecreaseStockExactlyOncePerOrder() throws Exception {
        Long productId = 80L;
        int orderQuantity = 1;
        OrderCommand command1 = OrderCommand.builder()
                .userId(801L)
                .productGrp(Map.of(
                        80L, orderQuantity
                ))
                .couponId(4L)
                .orderAddressInfo(address())
                .build();
        OrderCommand command2 = OrderCommand.builder()
                .userId(802L)
                .productGrp(Map.of(
                        80L, orderQuantity
                ))
                .couponId(4L)
                .orderAddressInfo(address())
                .build();

        Product productBefore = productRepository.findById(productId);
        int stockBefore = productBefore.getStock();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Runnable orderTaskA = () -> {
            try {
                startLatch.await();
                orderFacade.order(command1);
            } catch (Exception e) {
                log.warn("Order A exception: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable orderTaskB = () -> {
            try {
                startLatch.await();
                orderFacade.order(command2);
            } catch (Exception e) {
                log.warn("Order B exception: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(orderTaskA);
        executor.submit(orderTaskB);

        // 동시에 시작!
        startLatch.countDown();
        // 둘 다 끝날 때까지 대기
        doneLatch.await();

        executor.shutdown();

        // --- 검증 ---
        Product productAfter = productRepository.findById(productId);
        int stockAfter = productAfter.getStock();

        log.debug("stockBefore: " + stockBefore);
        log.debug("stockAfter: " + stockAfter);

        // 취소 2건이 각각 1개씩만 복구되어야 함 (중복/누락 없이)
        assertEquals(stockBefore - (2 * orderQuantity), stockAfter,
                "동시 주문 시 재고가 2건만큼 정확히 차감되어야 함");
    }

}