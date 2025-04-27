package kr.hhplus.be.server.infra.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccountHistRepositoryImpl implements AccountHistRepository {

    private final JpaAccountHistRepository jpaAccountHistRepository;

    @Override
    public List<AccountHistory> getAllByAccountId(Long AccountId) {
        return jpaAccountHistRepository.findAllByAccountId(AccountId);
    }

    @Override
    public void save(AccountHistory accountHistory) {
        jpaAccountHistRepository.save(accountHistory);
    }
}
