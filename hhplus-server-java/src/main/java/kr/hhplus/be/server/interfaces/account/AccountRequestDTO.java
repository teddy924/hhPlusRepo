package kr.hhplus.be.server.interfaces.account;

import kr.hhplus.be.server.application.account.AccountCommand;
import lombok.Builder;

@Builder
public record AccountRequestDTO (
        Long userId,
        Long amount
) {

    public AccountCommand toCommand() {
        return new AccountCommand(userId, amount);
    }
}
