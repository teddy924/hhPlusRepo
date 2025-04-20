package kr.hhplus.be.server.interfaces.account;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.account.AccountResult;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "계정 Response DTO")
public class AccountResponseDTO {
    @Schema(description = "유저 ID") private Long id;
    @Schema(description = "잔액") private Long balance;

    public static AccountResponseDTO from(AccountResult queryDto) {
        return new AccountResponseDTO(queryDto.userId(), queryDto.balance());
    }
}
