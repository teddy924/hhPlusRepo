package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountService;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountCommand {

    private final AccountService accountService;
    private final AccountHistRepository accountHistRepository;

    // 잔액 충전
    public void chargeAmount(Account account, Long amount) throws Exception {

        accountService.chargeAmount(account, amount);

    }

    // 잔액 사용
    public void useAmount(Account account, Long amount) throws Exception {

        accountService.useAmount(account, amount);

    }

    // 잔액 변동 이력 저장
    public void saveHist(Account account, Long amount, AccountHistType histType) throws Exception {

        AccountHistory history = AccountHistory.builder()
                .accountId(account.getId())
                .status(histType)
                .balance(amount)
                .build();

        accountHistRepository.save(history);
    }

}
