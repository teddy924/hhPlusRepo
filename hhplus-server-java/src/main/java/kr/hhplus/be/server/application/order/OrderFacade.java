package kr.hhplus.be.server.application.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {


    public void order() {

        // 1. 상품 확인 (재고/기간 확인)


        // 2. 쿠폰 확인 (보유 여부 + 유효성)

        // 3. 쿠폰 적용 (할인 금액 계산 + 사용 처리)

        // 4. 결제 처리 (실제 결제 요청 + 이력 저장)

        // 5. 주문 저장 (order, order_item, order_payment 등 테이블)

    }

}
