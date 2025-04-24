package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHistRepository accountHistRepository;

    // 잔액 충전
    @Transactional
    public void chargeAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.getByUserId(info.userId());

        account.charge(info.amount());

        accountRepository.save(account);

        saveHist(info, AccountHistType.CHARGE);

    }

    // 잔액 사용
    @Transactional
    public void useAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.getByUserId(info.userId());

        if (account.canUse(info.amount())) {
            account.use(info.amount());
        }

        accountRepository.save(account);

        saveHist(info, AccountHistType.USE);

    }

    // 잔액 조회
    @Transactional
    public AccountResult retrieveAccount(Long userId) throws Exception {

        Account account = accountRepository.getByUserId(userId);

        return new AccountResult(account.getUser().getId(), account.getBalance());

    }

    // 잔액 변동 이력 조회
    @Transactional
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
    @Transactional
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
