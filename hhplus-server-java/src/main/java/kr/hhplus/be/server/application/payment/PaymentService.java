package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void payment (PaymentInfo paymentInfo) {

        paymentRepository.save(paymentInfo);

    }

}
