package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AccountService accountService;
    private final AccountProperties accountProperties;

    public void charge(AccountCommand command) throws Exception {

        AccountInfo info = command.toInfo();

        accountService.chargeAmount(info);

//        int retryCnt = 0;
//
//        while(true) {
//            try {
//                accountService.chargeAmount(info);
//                break;
//            } catch (OptimisticLockException e) {
//                if (++retryCnt > accountProperties.maxRetry()) {
//                    throw new CustomException(FAIL_CHARGE_AMOUNT);
//                }
//                try {
//                    Thread.sleep(accountProperties.retryWaitMs());
//                } catch (InterruptedException e1) {
//                    Thread.currentThread().interrupt();
//                    log.warn(e1.getMessage());
//                }
//            }
//        }

    }

    public void use(AccountCommand command) throws Exception {

        AccountInfo info = command.toInfo();

        accountService.useAmount(info);

//        int retryCnt = 0;
//
//        while(true) {
//            try {
//                accountService.useAmount(info);
//                break;
//            } catch (OptimisticLockException e) {
//                if (++retryCnt > accountProperties.maxRetry()) {
//                    throw new CustomException(FAIL_CHARGE_AMOUNT);
//                }
//                try {
//                    Thread.sleep(accountProperties.retryWaitMs());
//                } catch (InterruptedException e1) {
//                    Thread.currentThread().interrupt();
//                    log.warn(e1.getMessage());
//                }
//            }
//        }

    }

}
