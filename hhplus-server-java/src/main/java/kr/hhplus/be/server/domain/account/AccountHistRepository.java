package kr.hhplus.be.server.domain.account;

import kr.hhplus.be.server.domain.account.entity.AccountHistory;

import java.util.List;

public interface AccountHistRepository {

    List<AccountHistory> getAllByAccountId(Long AccountId);

    void save(AccountHistory accountHistory);

}
