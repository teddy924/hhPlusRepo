package kr.hhplus.be.server.interfaces.account;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.account.AccountCommand;
import lombok.Builder;

@Builder
@Schema(description = "계정 Request DTO")
public record AccountRequestDTO (
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "금액(충전/사용)") Long amount
) {

    public AccountCommand toCommand() {
        return new AccountCommand(userId, amount);
    }
}
