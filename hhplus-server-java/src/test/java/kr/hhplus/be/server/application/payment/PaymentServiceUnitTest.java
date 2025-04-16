package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.common.exception.CustomException;
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
        // given
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .orderId(100L)
                .amount(20000L)
                .method(PaymentMethod.CASH)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        // when
        paymentService.save(paymentInfo);

        // then
        verify(paymentRepository).save(Payment.builder()
                .orderId(paymentInfo.orderId())
                .amount(paymentInfo.amount())
                .paymentMethod(paymentInfo.method())
                .paymentStatus(paymentInfo.status())
                .paidDt(paymentInfo.paidAt())
                .sysCretDt(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 성공 시 Payment 객체를 반환")
    void retrieveByOrderId_success() {
        // given
        Payment payment = Payment.builder()
                .orderId(100L)
                .amount(20000L)
                .build();

        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.of(payment));

        // when
        Payment result = paymentService.retrieveByOrderId(100L);

        // then
        assertEquals(20000L, result.getAmount());
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
