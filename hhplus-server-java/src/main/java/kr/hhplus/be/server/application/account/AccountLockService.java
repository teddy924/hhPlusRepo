package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.common.DistributedLockExecutor;
import kr.hhplus.be.server.common.LockKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.LockAcquireFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Slf4j
@Service
public class AccountLockService {

    private final DistributedLockExecutor lockExecutor;
    private final AccountService accountService;

    public AccountLockService(DistributedLockExecutor lockExecutor, AccountService accountService) {
        this.lockExecutor = lockExecutor;
        this.accountService = accountService;
    }

    public void chargeAmountLock(AccountCommand command) {
        int retryCount = 5;
        String lockKey = LockKey.account(command.userId());

        while (retryCount-- > 0) {
            log.debug("retryCount: " + retryCount);
            try {
                lockExecutor.runWithLock(lockKey, () -> {
                    try {
                        accountService.chargeAmount(command.toInfo());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return; // 락 획득 및 실행 성공 시 종료
            } catch (LockAcquireFailException e1) {
                // 락 획득 실패 → 재시도
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();  // 인터럽트 상태 복원
                    throw new RuntimeException("Retry sleep interrupted while charge Amount", e2);
                }
            }
        }

        // 재시도 실패 시 예외 발생
        throw new CustomException(FAIL_CHARGE_AMOUNT);
    }

    public void useAmountLock(AccountCommand command) {
        int retryCount = 5;
        String lockKey = LockKey.account(command.userId());

        while (retryCount-- > 0) {
            try {
                lockExecutor.runWithLock(lockKey, () -> {
                    try {
                        accountService.useAmount(command.toInfo());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return; // 락 획득 및 실행 성공 시 종료
            } catch (LockAcquireFailException e1) {
                // 락 획득 실패 → 재시도
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();  // 인터럽트 상태 복원
                    throw new RuntimeException("Retry sleep interrupted while use Amount", e2);
                }
            }
        }

        // 재시도 실패 시 예외 발생
        throw new CustomException(FAIL_UES_AMOUNT);
    }
}
