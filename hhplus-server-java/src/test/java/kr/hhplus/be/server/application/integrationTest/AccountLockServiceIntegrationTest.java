package kr.hhplus.be.server.application.integrationTest;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.account.AccountCommand;
import kr.hhplus.be.server.application.account.AccountLockService;
import kr.hhplus.be.server.config.EmbeddedRedisConfig;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@Import(EmbeddedRedisConfig.class)
public class AccountLockServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AccountLockServiceIntegrationTest.class);

    @Autowired
    AccountLockService accountLockService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("잔액 연속 충전 동시성 이슈 테스트")
    void charge_concurrencyIssue() throws Exception {
        Long userId = 6L;
        long chargeAmount = 1000L;
        int threadCount = 10;

        // 잔액 충전을 연속으로 눌렀을 때 정상 충전 확인
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {             // 쓰레드 풀에 해당 작업을 할당
                try {
                    accountLockService.chargeAmountLock(new AccountCommand(userId, chargeAmount));
                } catch (Exception e) {
                    log.debug(e.getMessage());
                } finally {                     // 결과에 상관없이 작업이 종료되면 thread 반환
                    latch.countDown();
                }
            });
        }

        latch.await();

        entityManager.clear();

        Account result = accountRepository.findByUserId(userId);
        long actualBalance = result.getBalance();
        long expectedBalance = 100000 + (chargeAmount * threadCount);

        System.out.println("actualBalance: " + actualBalance);
        System.out.println("expectedBalance: " + expectedBalance);

        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    @DisplayName("잔액 연속 사용 동시성 이슈 테스트")
    void use_concurrencyIssue() throws Exception {
        Long userId = 7L;
        long chargeAmount = 500L;
        int threadCount = 10;

        // 잔액 사용을 연속으로 눌렀을 때 정상 충전 확인
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {             // 쓰레드 풀에 해당 작업을 할당
                try {
                    accountLockService.useAmountLock(new AccountCommand(userId, chargeAmount));
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                } finally {                     // 결과에 상관없이 작업이 종료되면 thread 반환
                    latch.countDown();
                }
            });
        }

        latch.await();

        entityManager.clear();

        Account result = accountRepository.findByUserId(userId);
        long actualBalance = result.getBalance();
        long expectedBalance = 100000 - (chargeAmount * threadCount);

        System.out.println("actualBalance: " + actualBalance);
        System.out.println("expectedBalance: " + expectedBalance);

        assertEquals(expectedBalance, actualBalance);
    }
}
