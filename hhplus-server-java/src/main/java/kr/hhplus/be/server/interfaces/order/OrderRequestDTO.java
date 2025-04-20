package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.OrderCommand;
import lombok.Builder;

import java.util.Map;

@Builder
@Schema(description = "주문-결제 Request DTO")
public record OrderRequestDTO (
        @Schema(description = "유저 ID", example = "1")
        Long userId,
        @Schema(description = "상품 정보", example = "1000")
        Map<Long, Long> productGrp,
        @Schema(description = "쿠폰 ID", example = "100")
        Long couponId,
        @Schema(description = "배송 정보")
        OrderAddressDTO orderAddressDTO

) {
        public OrderCommand toCommand () {
                return new OrderCommand(userId, productGrp, couponId, orderAddressDTO.toInfo());
        }
}
