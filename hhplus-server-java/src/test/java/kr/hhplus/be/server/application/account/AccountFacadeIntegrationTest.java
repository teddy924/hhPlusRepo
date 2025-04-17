package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class AccountFacadeIntegrationTest extends IntegrationTestBase {

    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHistRepository accountHistRepository;

    @Test
    @DisplayName("충전 정상 흐름 - 잔액 증가 및 CHARGE 이력 저장")
    void charge_success_shouldIncreaseBalanceAndSaveHistory() {
        // given
        Long userId = 1L;
        Long chargeAmount = 5000L;
        AccountCommand command = new AccountCommand(userId, chargeAmount);
        Account account = accountRepository.getByUserId(userId);

        assertDoesNotThrow(() -> accountFacade.charge(command));

        List<AccountHistory> histories = accountHistRepository.getAllByAccountId(account.getId());
        assertFalse(histories.isEmpty());
        assertEquals(AccountHistType.CHARGE, histories.get(histories.size() - 1).getStatus());
    }

    @Test
    @DisplayName("사용 정상 흐름 - 잔액 감소 및 USE 이력 저장")
    void use_success_shouldDecreaseBalanceAndSaveHistory() {
        // given
        Long userId = 2L;
        long initialBalance = 100_000L;
        long useAmount = 3000L;

        AccountCommand command = new AccountCommand(userId, useAmount);

        // when
        assertDoesNotThrow(() -> accountFacade.use(command));

        // then
        Account account = accountRepository.getByUserId(userId);
        assertEquals(initialBalance - useAmount, account.getBalance());

        List<AccountHistory> histories = accountHistRepository.getAllByAccountId(account.getId());
        assertFalse(histories.isEmpty());
        assertEquals(AccountHistType.USE, histories.get(histories.size() - 1).getStatus());
    }

    @Test
    @DisplayName("음수 금액 충전 시 예외 발생")
    void charge_withNegativeAmount_shouldThrowException() {
        // given
        Long userId = 3L;
        long invalidAmount = -1000L;
        AccountCommand command = new AccountCommand(userId, invalidAmount);

        // when & then
        Exception exception = assertThrows(CustomException.class, () -> accountFacade.charge(command));
        assertTrue(exception.getMessage().contains("충전/사용 금액은 100원 단위의 100원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 충전 시 예외 발생")
    void charge_withInvalidUserId_shouldThrowException() {
        // given
        Long invalidUserId = 9999L;
        AccountCommand command = new AccountCommand(invalidUserId, 1000L);

        // when & then
        Exception exception = assertThrows(CustomException.class, () -> accountFacade.charge(command));
        assertTrue(exception.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("충전 후 잔액이 정확히 반영되었는지 검증")
    void charge_shouldReflectCorrectBalance() throws Exception {
        // given
        Long userId = 4L;
        long initialBalance = 100_000L;
        long charge = 3000L;

        AccountCommand command = new AccountCommand(userId, charge);

        // when
        accountFacade.charge(command);

        // then
        Account account = accountRepository.getByUserId(userId);
        assertEquals(initialBalance + charge, account.getBalance());
    }

    @Test
    @DisplayName("잔액보다 큰 금액 사용 시 예외 발생")
    void use_withInsufficientBalance_shouldThrowException() {
        // given
        Long userId = 5L;

        AccountCommand command = new AccountCommand(userId, 500_000L);

        // when & then
        Exception exception = assertThrows(CustomException.class, () -> accountFacade.use(command));
        assertTrue(exception.getMessage().contains("잔액이 부족합니다."));
    }

}