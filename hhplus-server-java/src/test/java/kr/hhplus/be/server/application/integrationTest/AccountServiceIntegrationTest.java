package kr.hhplus.be.server.application.integrationTest;

import kr.hhplus.be.server.application.account.AccountHistResult;
import kr.hhplus.be.server.application.account.AccountResult;
import kr.hhplus.be.server.application.account.AccountService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.EmbeddedRedisConfig;
import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Import(EmbeddedRedisConfig.class)
class AccountServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceIntegrationTest.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private AccountHistRepository accountHistRepository;

    @Test
    @DisplayName("잔액 충전 시 잔액이 증가해야 한다")
    void chargeAmount_shouldIncreaseBalance() throws Exception {
        Long userId = 11L;
        long chargeAmount = 5000L;

        accountService.chargeAmount(new AccountInfo(userId, chargeAmount));
        Account result = accountRepository.findByUserId(userId);

        assertEquals(100000L + chargeAmount, result.getBalance());
    }

    @Test
    @DisplayName("잔액 사용 시 잔액이 감소해야 한다")
    void useAmount_shouldDecreaseBalance() throws Exception {
        Long userId = 12L;
        long useAmount = 3000L;

        accountService.useAmount(new AccountInfo(userId, useAmount));
        Account result = accountRepository.findByUserId(userId);

        assertEquals(100000L - useAmount, result.getBalance());
    }

    @Test
    @DisplayName("잔액 부족 시 예외가 발생해야 한다")
    void useAmount_shouldThrow_whenInsufficient() {
        Long userId = 13L;
        long useAmount = 999_999L;

        CustomException exception = assertThrows(CustomException.class,
                () -> accountService.useAmount(new AccountInfo(userId, useAmount)));

        assertTrue(exception.getMessage().contains("잔액이 부족합니다."));
    }

    @Test
    @DisplayName("잔액을 조회할 수 있다")
    void retrieveAccount_shouldReturnCorrectInfo() throws Exception {
        Long userId = 14L;

        AccountResult result = accountService.retrieveAccount(userId);

        assertEquals(userId, result.userId());
        assertTrue(result.balance() > 0);
    }

    @Test
    @DisplayName("잔액 변동 이력을 조회할 수 있다")
    void retrieveAccountHist_shouldReturnHistoryList() throws Exception {
        Long userId = 15L;

        List<AccountHistResult> results = accountService.retrieveAccountHist(userId);

        assertFalse(results.isEmpty());
        assertEquals(AccountHistType.CHARGE, results.get(0).status());
    }

    @Test
    @DisplayName("잔액 변동 이력을 저장할 수 있다")
    void saveHist_shouldInsertHistory() {
        // given
        Long userId = 16L;
        long amount = 3000L;
        AccountInfo info = new AccountInfo(userId, amount);

        Account account = accountRepository.findByUserId(userId);
        List<AccountHistory> initHistories = accountHistRepository.getAllByAccountId(account.getId());

        accountService.saveHist(info, AccountHistType.USE);

        List<AccountHistory> histories = accountHistRepository.getAllByAccountId(account.getId())
                .stream()
                .filter(h -> h.getSysCretDt() != null)
                .sorted(Comparator.comparing(AccountHistory::getSysCretDt).reversed())
                .toList();

        assertEquals(initHistories.size() + 1, histories.size());
        assertEquals(AccountHistType.USE, histories.get(0).getStatus());
        assertEquals(amount, histories.get(0).getAmount());
    }

    @Test
    @DisplayName("잔액보다 큰 금액 사용 시 예외 발생")
    void use_withInsufficientBalance_shouldThrowException() {
        // given
        Long userId = 17L;
        long amount = 500_000L;

        AccountInfo info = new AccountInfo(userId, 500_000L);

        // when & then
        Exception exception = assertThrows(CustomException.class, () -> accountService.useAmount(info));
        log.debug(exception.getMessage());
        assertTrue(exception.getMessage().contains("잔액이 부족합니다."));
    }

    @Test
    @DisplayName("음수 금액 충전 시 예외 발생")
    void charge_withNegativeAmount_shouldThrowException() {
        // given
        Long userId = 3L;
        long invalidAmount = -1000L;
        AccountInfo info = new AccountInfo(userId, invalidAmount);

        // when & then
        Exception exception = assertThrows(CustomException.class, () -> accountService.chargeAmount(info));
        assertTrue(exception.getMessage().contains("충전/사용 금액은 100원 단위의 100원 이상이어야 합니다."));
    }

}