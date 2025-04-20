package kr.hhplus.be.server.application.account;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Transactional
class AccountServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private AccountHistRepository accountHistRepository;

    @Test
    @DisplayName("잔액 충전 시 잔액이 증가해야 한다")
    void chargeAmount_shouldIncreaseBalance() throws Exception {
        Long userId = 1L;
        long chargeAmount = 5000L;

        accountService.chargeAmount(new AccountInfo(userId, chargeAmount));
        Account result = accountRepository.getByUserId(userId);

        assertEquals(100000L + chargeAmount, result.getBalance());
    }

    @Test
    @DisplayName("잔액 사용 시 잔액이 감소해야 한다")
    void useAmount_shouldDecreaseBalance() throws Exception {
        Long userId = 2L;
        long useAmount = 3000L;

        accountService.useAmount(new AccountInfo(userId, useAmount));
        Account result = accountRepository.getByUserId(userId);

        assertEquals(100000L - useAmount, result.getBalance());
    }

    @Test
    @DisplayName("잔액 부족 시 예외가 발생해야 한다")
    void useAmount_shouldThrow_whenInsufficient() {
        Long userId = 3L;
        long useAmount = 999_999L;

        CustomException exception = assertThrows(CustomException.class,
                () -> accountService.useAmount(new AccountInfo(userId, useAmount)));

        assertTrue(exception.getMessage().contains("잔액이 부족합니다."));
    }

    @Test
    @DisplayName("잔액을 조회할 수 있다")
    void retrieveAccount_shouldReturnCorrectInfo() throws Exception {
        Long userId = 4L;

        AccountResult result = accountService.retrieveAccount(userId);

        assertEquals(userId, result.userId());
        assertTrue(result.balance() > 0);
    }

    @Test
    @DisplayName("잔액 변동 이력을 조회할 수 있다")
    void retrieveAccountHist_shouldReturnHistoryList() throws Exception {
        Long userId = 1L;

        List<AccountHistResult> results = accountService.retrieveAccountHist(userId);

        assertFalse(results.isEmpty());
        assertEquals(AccountHistType.CHARGE, results.get(0).status());
    }

    @Test
    @DisplayName("잔액 변동 이력을 저장할 수 있다")
    void saveHist_shouldInsertHistory() {
        // given
        Long userId = 6L;
        long amount = 3000L;
        AccountInfo info = new AccountInfo(userId, amount);

        Account account = accountRepository.getByUserId(userId);
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
}