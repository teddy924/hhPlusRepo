package kr.hhplus.be.server.infra.account;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.hhplus.be.server.config.swagger.ErrorCode.NOT_EXIST_ACCOUNT;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Account getByUserId(Long userId) {
        return jpaAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_ACCOUNT));
    }

    @Override
    public void save(Account account) {
        jpaAccountRepository.save(account);
    }
}
