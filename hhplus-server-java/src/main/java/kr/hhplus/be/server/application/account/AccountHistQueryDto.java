package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;

import java.time.LocalDateTime;

public record AccountHistQueryDto(
        Long accountId
        , Long balance
        , AccountHistType status
        , LocalDateTime sysCretDt
) {

    public static AccountHistQueryDto from (AccountHistory history) {
        return new AccountHistQueryDto(
                history.getAccountId()
                , history.getBalance()
                , history.getStatus()
                , history.getSysCretDt()
        );
    }

}
