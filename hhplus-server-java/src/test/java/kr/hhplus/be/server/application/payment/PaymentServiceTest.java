package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제 정보 저장 시 repository의 save가 호출되어야 한다")
    void save_shouldCallRepositorySave() {
        // given
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .orderId(100L)
                .amount(20000L)
                .build();

        // when
        paymentService.save(paymentInfo);

        // then
        verify(paymentRepository).save(paymentInfo);
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 성공 시 Payment 객체를 반환해야 한다")
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
    @DisplayName("주문 ID로 결제 조회 시 존재하지 않으면 예외가 발생해야 한다")
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
