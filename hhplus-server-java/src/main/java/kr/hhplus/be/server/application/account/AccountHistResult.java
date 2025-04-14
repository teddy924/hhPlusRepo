package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;

import java.time.LocalDateTime;

public record AccountHistResult(
        Long accountId
        , Long balance
        , AccountHistType status
        , LocalDateTime sysCretDt
) {

    public static AccountHistResult from (AccountHistory history) {
        return new AccountHistResult(
                history.getAccountId()
                , history.getBalance()
                , history.getStatus()
                , history.getSysCretDt()
        );
    }

}
