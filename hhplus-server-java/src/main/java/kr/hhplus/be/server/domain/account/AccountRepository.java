package kr.hhplus.be.server.domain.account;

import kr.hhplus.be.server.domain.account.entity.Account;

import java.util.Optional;


public interface AccountRepository {

    Optional<Account> findByUserId(Long userId);

    void save(Account account);

}
