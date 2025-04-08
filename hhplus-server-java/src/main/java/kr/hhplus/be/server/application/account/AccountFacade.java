package kr.hhplus.be.server.application.account;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.account.AccountHistType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AccountService accountService;

    @Transactional
    public void charge(AccountCommand command) throws Exception {

        accountService.chargeAmount(command);
        accountService.saveHist(command, AccountHistType.CHARGE);
    }

    @Transactional
    public void use(AccountCommand command) throws Exception {

        accountService.useAmount(command);
        accountService.saveHist(command, AccountHistType.USE);
    }

}
