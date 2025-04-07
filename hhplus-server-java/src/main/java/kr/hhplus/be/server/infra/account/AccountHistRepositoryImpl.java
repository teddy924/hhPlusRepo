package kr.hhplus.be.server.infra.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class AccountHistRepositoryImpl implements AccountHistRepository {

    @Override
    public List<AccountHistory> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void save(AccountHistory accountHistory) {

    }
}
