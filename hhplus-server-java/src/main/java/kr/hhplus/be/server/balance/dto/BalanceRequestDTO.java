package kr.hhplus.be.server.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceRequestDTO {
    @Schema(description = "유저 ID", example = "1")
    private Long id;
    @Schema(description = "충전 금액", example = "10000")
    private Long amount;
}
