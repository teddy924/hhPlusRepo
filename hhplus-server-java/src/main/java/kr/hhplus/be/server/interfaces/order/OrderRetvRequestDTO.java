package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "주문 조회 요청 DTO")
public class OrderRetvRequestDTO {
    @Schema(description = "주문 ID", example = "403020102002")
    private Long orderId;
}
