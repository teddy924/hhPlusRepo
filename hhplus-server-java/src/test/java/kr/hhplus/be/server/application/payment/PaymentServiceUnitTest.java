package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제 정보 저장")
    void save_shouldCallRepositorySave() {
        Order order = Order.builder()
                .id(100L)
                .build();

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .orderId(order.getId())
                .amount(20000L)
                .method(PaymentMethod.CASH)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        // when
        paymentService.save(order, paymentInfo);

        // then
        verify(paymentRepository).save(argThat(saved ->
                saved.getOrder().getId().equals(order.getId()) &&
                        saved.getAmount().equals(20000L) &&
                        saved.getPaymentMethod() == PaymentMethod.CASH &&
                        saved.getPaymentStatus() == PaymentStatus.COMPLETED &&
                        saved.getPaidDt().equals(paymentInfo.paidAt())
        ));
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 성공 시 Payment 객체를 반환")
    void retrieveByOrderId_success() {
        Order order = Order.builder()
                .id(100L)
                .build();

        Payment payment = Payment.builder()
                .order(order)
                .amount(20000L)
                .build();

        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.of(payment));

        Payment result = paymentService.retrieveByOrderId(order.getId());

        assertEquals(20000L, result.getAmount());
        assertEquals(100L, result.getOrder().getId());
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 시 존재하지 않으면 예외 발생")
    void retrieveByOrderId_shouldThrow_whenNotFound() {
        // given
        when(paymentRepository.findByOrderId(999L)).thenReturn(Optional.empty());

        // when
        CustomException ex = assertThrows(CustomException.class, () ->
                paymentService.retrieveByOrderId(999L));

        // then
        assertTrue(ex.getMessage().contains("해당 결제 정보를 찾을 수 없습니다."));
    }
}
