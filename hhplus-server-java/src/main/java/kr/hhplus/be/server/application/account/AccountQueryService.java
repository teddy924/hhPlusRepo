package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountQueryService {

    private final AccountRepository accountRepository;
    private final AccountHistRepository accountHistoryRepository;

    public AccountQueryDto retrieveAccount(Long userId) throws Exception {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        return new AccountQueryDto(account.getUserId(), account.getBalnace());

    }

    public List<AccountHistQueryDto> retrieveAccountHist(Long userId) throws Exception {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        List<AccountHistory> histories = accountHistoryRepository.findAllByUserId(account.getUserId());

        return histories.stream().map(AccountHistQueryDto::from).toList();

    }

}
