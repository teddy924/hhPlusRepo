package kr.hhplus.be.server.infra.account;

import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {


    @Override
    public Optional<Account> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public void save(Account account) {

    }
}
