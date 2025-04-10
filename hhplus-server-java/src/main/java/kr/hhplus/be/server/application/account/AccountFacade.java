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

        AccountInfo info = command.toInfo();

        accountService.chargeAmount(info);
        accountService.saveHist(info, AccountHistType.CHARGE);
    }

    @Transactional
    public void use(AccountCommand command) throws Exception {

        AccountInfo info = command.toInfo();

        accountService.useAmount(info);
        accountService.saveHist(info, AccountHistType.USE);
    }

}
