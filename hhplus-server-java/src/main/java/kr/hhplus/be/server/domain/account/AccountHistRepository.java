package kr.hhplus.be.server.domain.account;

import kr.hhplus.be.server.domain.account.entity.AccountHistory;

import java.util.List;

public interface AccountHistRepository {

    List<AccountHistory> findAllByAccountId(Long AccountId);

    void save(AccountHistory accountHistory);

}
