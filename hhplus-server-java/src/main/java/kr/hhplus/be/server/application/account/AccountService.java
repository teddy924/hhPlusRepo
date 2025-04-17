package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
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

    }

    // 잔액 사용
    public void useAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.getByUserId(info.userId());

        if (account.canUse(info.amount())) {
            account.use(info.amount());
        }

    }

    // 잔액 조회
    public AccountResult retrieveAccount(Long userId) throws Exception {

        Account account = accountRepository.getByUserId(userId);

        return new AccountResult(account.getUser().getId(), account.getBalance());

    }

    // 잔액 변동 이력 조회
    public List<AccountHistResult> retrieveAccountHist(Long userId) throws Exception {

        Account account = accountRepository.getByUserId(userId);

        List<AccountHistory> histories = accountHistRepository.getAllByAccountId(account.getId())
                .stream()
                .sorted(Comparator.comparing(AccountHistory::getSysCretDt).reversed())
                .toList();

        return histories.stream()
                .map(AccountHistResult::from)
                .toList();


    }

    // 잔액 변동 이력 저장
    public void saveHist(AccountInfo info, AccountHistType histType) {

        Account account = accountRepository.getByUserId(info.userId());

        AccountHistory history = AccountHistory.builder()
                .account(account) // 연관된 Account 엔티티 직접 설정
                .status(histType)
                .amount(info.amount())
                .sysCretDt(LocalDateTime.now())
                .build();

        accountHistRepository.save(history);

    }

}
