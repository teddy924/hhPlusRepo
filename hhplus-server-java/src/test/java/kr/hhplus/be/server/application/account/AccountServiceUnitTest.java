package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceUnitTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHistRepository accountHistRepository;

    @Test
    @DisplayName("잔액 충전 시 계좌에서 charge 호출 후 저장되어야 한다")
    void chargeAmount_shouldIncreaseBalanceAndSave() throws Exception {
        // given
        AccountInfo info = new AccountInfo(1L, 1000L);
        Account mockAccount = mock(Account.class);
        when(accountRepository.getByUserId(1L)).thenReturn(mockAccount);

        // when
        accountService.chargeAmount(info);

        // then
        verify(mockAccount).charge(1000L);
        verify(accountRepository).save(mockAccount);
    }

    @Test
    @DisplayName("잔액 사용 시 사용 가능 금액이면 use 호출 후 저장되어야 한다")
    void useAmount_shouldUseBalanceAndSave() throws Exception {
        // given
        AccountInfo info = new AccountInfo(1L, 500L);
        Account mockAccount = mock(Account.class);
        when(accountRepository.getByUserId(1L)).thenReturn(mockAccount);
        when(mockAccount.canUse(500L)).thenReturn(true);

        // when
        accountService.useAmount(info);

        // then
        verify(mockAccount).use(500L);
        verify(accountRepository).save(mockAccount);
    }

    @Test
    @DisplayName("잔액 조회 시 해당 유저의 잔액이 반환되어야 한다")
    void retrieveAccount_shouldReturnAccount() throws Exception {
        // given
        Account account = new Account(1L, 1L, 5000L, LocalDateTime.now(), LocalDateTime.now());
        when(accountRepository.getByUserId(1L)).thenReturn(account);

        // when
        AccountResult result = accountService.retrieveAccount(1L);

        // then
        assertEquals(1L, result.userId());
        assertEquals(5000L, result.balance());
    }

    @Test
    @DisplayName("잔액 이력 조회 시 유저 기준으로 히스토리 리스트가 반환되어야 한다")
    void retrieveAccountHist_shouldReturnHistoryList() throws Exception {
        // given
        Account account = new Account(1L, 3L, 1000L, LocalDateTime.now(), LocalDateTime.now());
        AccountHistory history1 = AccountHistory.builder()
                .id(1L)
                .accountId(1L)
                .status(AccountHistType.CHARGE)
                .balance(1000L)
                .sysCretDt(LocalDateTime.now())
                .build();

        AccountHistory history2 = AccountHistory.builder()
                .id(2L)
                .accountId(1L)
                .status(AccountHistType.USE)
                .balance(500L)
                .sysCretDt(LocalDateTime.now())
                .build();

        when(accountRepository.getByUserId(3L)).thenReturn(account);
        when(accountHistRepository.getAllByAccountId(1L)).thenReturn(List.of(history1, history2));

        // when
        List<AccountHistResult> result = accountService.retrieveAccountHist(3L);

        // then
        assertEquals(2, result.size());
        assertEquals(1000L, result.get(0).balance());
    }

    @Test
    @DisplayName("히스토리 저장 시 입력값 기준으로 AccountHistory가 저장되어야 한다")
    void saveHist_shouldCallSaveWithCorrectData() {
        // given
        AccountInfo info = new AccountInfo(1L, 300L);
        AccountHistType type = AccountHistType.CHARGE;

        // when
        accountService.saveHist(info, type);

        // then
        verify(accountHistRepository).save(argThat(h ->
                h.getAccountId().equals(1L) &&
                        h.getStatus() == AccountHistType.CHARGE &&
                        h.getBalance().equals(300L)
        ));
    }

    @Test
    @DisplayName("잔액 충전 시 존재하지 않는 유저일 경우 예외가 발생해야 한다")
    void chargeAmount_shouldThrowException_whenUserNotFound() {
        // given
        AccountInfo info = new AccountInfo(999L, 1000L);
        when(accountRepository.getByUserId(999L)).thenReturn(null);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.chargeAmount(info));
        assertTrue(ex.getMessage().contains("사용자가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 사용 시 존재하지 않는 유저일 경우 예외가 발생해야 한다")
    void useAmount_shouldThrowException_whenUserNotFound() {
        // given
        AccountInfo info = new AccountInfo(999L, 500L);
        when(accountRepository.getByUserId(999L)).thenReturn(null);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.useAmount(info));
        assertTrue(ex.getMessage().contains("사용자가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 조회 시 존재하지 않는 유저일 경우 예외가 발생해야 한다")
    void retrieveAccount_shouldThrowException_whenUserNotFound() {
        // given
        when(accountRepository.getByUserId(999L)).thenReturn(null);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.retrieveAccount(999L));
        assertTrue(ex.getMessage().contains("사용자가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 이력 조회 시 존재하지 않는 유저일 경우 예외가 발생해야 한다")
    void retrieveAccountHist_shouldThrowException_whenUserNotFound() {
        // given
        when(accountRepository.getByUserId(999L)).thenReturn(null);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.retrieveAccountHist(999L));
        assertTrue(ex.getMessage().contains("사용자가 존재하지 않습니다."));
    }
}
