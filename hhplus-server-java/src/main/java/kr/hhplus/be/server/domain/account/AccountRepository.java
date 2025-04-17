package kr.hhplus.be.server.domain.account;

import kr.hhplus.be.server.domain.account.entity.Account;


public interface AccountRepository {

    Account getByUserId(Long userId);

    void save(Account account);

}
