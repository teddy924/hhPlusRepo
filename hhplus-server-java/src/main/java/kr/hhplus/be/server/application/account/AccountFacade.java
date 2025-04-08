package kr.hhplus.be.server.application.account;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.interfaces.account.AccountRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AccountRepository accountRepository;
    private final AccountCommand accountCmdService;

    @Transactional
    public void charge(AccountRequestDTO accountRequestDTO) throws Exception {
        Account account = accountRepository.findByUserId(accountRequestDTO.getId())
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        accountCmdService.chargeAmount(account, accountRequestDTO.getAmount());
        accountCmdService.saveHist(account, accountRequestDTO.getAmount(), AccountHistType.CHARGE);
    }

    @Transactional
    public void use(AccountRequestDTO accountRequestDTO) throws Exception {
        Account account = accountRepository.findByUserId(accountRequestDTO.getId())
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        accountCmdService.useAmount(account, accountRequestDTO.getAmount());
        accountCmdService.saveHist(account, accountRequestDTO.getAmount(), AccountHistType.USE);
    }

}
