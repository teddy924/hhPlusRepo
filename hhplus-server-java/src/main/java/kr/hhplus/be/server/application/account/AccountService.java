package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHistRepository accountHistRepository;

    // 잔액 충전
    public void chargeAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.getByUserId(info.userId());

        account.charge(info.amount());

        accountRepository.save(account);
    }

    // 잔액 사용
    public void useAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.getByUserId(info.userId());

        if (account.canUse(info.amount())) {
            account.use(info.amount());
        }

        accountRepository.save(account);

    }

    // 잔액 조회
    public AccountResult retrieveAccount(Long userId) throws Exception {

        Account account = accountRepository.getByUserId(userId);

        return new AccountResult(account.getUserId(), account.getBalance());

    }

    // 잔액 변동 이력 조회
    public List<AccountHistResult> retrieveAccountHist(Long userId) throws Exception {

        Account account = accountRepository.getByUserId(userId);

        List<AccountHistory> histories = accountHistRepository.getAllByAccountId(account.getId());

        return histories.stream()
                .map(AccountHistResult::from)
                .toList();


    }

    // 잔액 변동 이력 저장
    public void saveHist(AccountInfo info, AccountHistType histType) {

        AccountHistory history = AccountHistory.builder()
                .accountId(info.userId())
                .status(histType)
                .balance(info.amount())
                .build();

        accountHistRepository.save(history);

    }

}
