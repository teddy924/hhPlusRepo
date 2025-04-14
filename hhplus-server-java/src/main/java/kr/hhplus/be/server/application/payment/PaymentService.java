package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save (PaymentInfo paymentInfo) {

        paymentRepository.save(paymentInfo);

    }

    public Payment retrieveByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_PAYMENT));
    }

}
