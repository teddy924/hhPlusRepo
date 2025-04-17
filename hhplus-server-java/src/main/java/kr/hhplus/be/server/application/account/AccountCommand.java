package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountInfo;
import lombok.Builder;

@Builder
public record AccountCommand(
        Long userId,
        Long amount
) {
    public AccountInfo toInfo() {
        return new AccountInfo(userId, amount);
    }
}
