package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import kr.hhplus.be.server.interfaces.account.AccountHistResponseDto;
import kr.hhplus.be.server.interfaces.account.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHistRepository accountHistRepository;

    // 잔액 충전
    public void chargeAmount (AccountCommand command) throws Exception {

        Account account = accountRepository.findByUserId(command.userId())
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        account.charge(command.amount());

        accountRepository.save(account);
    }

    // 잔액 사용
    public void useAmount (AccountCommand command) throws Exception {

        Account account = accountRepository.findByUserId(command.userId())
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        if (account.canUse(command.amount())) {
            account.use(command.amount());
        }

        accountRepository.save(account);

    }

    // 잔액 조회
    public AccountResponseDTO retrieveAccount(Long userId) throws Exception {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        return AccountResponseDTO.from(new AccountResult(account.getUserId(), account.getBalance()));

    }

    // 잔액 변동 이력 조회
    public AccountHistResponseDto retrieveAccountHist(Long userId) throws Exception {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        List<AccountHistory> histories = accountHistRepository.findAllByUserId(account.getUserId());

        return AccountHistResponseDto.from(histories.stream()
                .map(AccountHistResult::from)
                .toList()
        );


    }

    // 잔액 변동 이력 저장
    public void saveHist(AccountCommand command, AccountHistType histType) {

        AccountHistory history = AccountHistory.builder()
                .accountId(command.userId())
                .status(histType)
                .balance(command.amount())
                .build();

        accountHistRepository.save(history);

    }

}
