package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "주문-결제 요청 DTO")
public class OrderRequestDTO {
    @Schema(description = "유저 ID", example = "1")
    private Long userId;
    @Schema(description = "상품 ID", example = "1020302010203040")
    private List<Long> productList;
    @Schema(description = "쿠폰 ID", example = "1230002")
    private Long couponId;
}
