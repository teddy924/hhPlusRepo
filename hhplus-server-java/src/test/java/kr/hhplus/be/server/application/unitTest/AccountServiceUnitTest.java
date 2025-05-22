package kr.hhplus.be.server.application.unitTest;

import kr.hhplus.be.server.application.account.AccountHistResult;
import kr.hhplus.be.server.application.account.AccountResult;
import kr.hhplus.be.server.application.account.AccountService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AccountServiceUnitTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHistRepository accountHistRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<Object> rBucket;

    @BeforeEach
    void setUp() {
        lenient().when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
    }

    @Test
    @DisplayName("잔액 충전 시 계좌에서 charge 호출 후 저장되어야 한다")
    void chargeAmount_shouldIncreaseBalanceAndSave() throws Exception {
        AccountInfo info = new AccountInfo(21L, 1000L);
        Account mockAccount = mock(Account.class);
        when(accountRepository.findByUserId(21L)).thenReturn(mockAccount);

        accountService.chargeAmount(info);

        verify(mockAccount).charge(1000L);
        verify(accountRepository).save(mockAccount);
    }

    @Test
    @DisplayName("잔액 사용 시 사용 가능 금액이면 use 호출 후 저장되어야 한다")
    void useAmount_shouldUseBalanceAndSave() throws Exception {
        AccountInfo info = new AccountInfo(21L, 500L);
        Account mockAccount = mock(Account.class);
        when(accountRepository.findByUserId(21L)).thenReturn(mockAccount);
        when(mockAccount.canUse(500L)).thenReturn(true);

        accountService.useAmount(info);

        verify(mockAccount).use(500L);
        verify(accountRepository).save(mockAccount);
    }

    @Test
    @DisplayName("잔액 조회 시 해당 유저의 잔액이 반환되어야 한다")
    void retrieveAccount_shouldReturnAccount() throws Exception {
        User user = User.builder().id(21L).name("홍길동").email("test@hhplus.kr").build();
        Account account = Account.builder().id(21L).user(user).balance(5000L).sysCretDt(LocalDateTime.now()).build();

        when(accountRepository.findByUserId(21L)).thenReturn(account);

        AccountResult result = accountService.retrieveAccount(21L);

        assertEquals(21L, result.userId());
        assertEquals(5000L, result.balance());
    }

    @Test
    @DisplayName("잔액 이력 조회 시 유저 기준으로 히스토리 리스트가 반환되어야 한다")
    void retrieveAccountHist_shouldReturnHistoryList() throws Exception {
        User user = User.builder().id(21L).name("홍길동").email("test@hhplus.kr").build();
        Account account = Account.builder().id(21L).user(user).balance(1000L).sysCretDt(LocalDateTime.now()).build();
        AccountHistory history1 = AccountHistory.builder().id(21L).account(account).status(AccountHistType.CHARGE).amount(1000L).sysCretDt(LocalDateTime.now().minusHours(1L)).build();
        AccountHistory history2 = AccountHistory.builder().id(22L).account(account).status(AccountHistType.USE).amount(500L).sysCretDt(LocalDateTime.now()).build();

        when(accountRepository.findByUserId(21L)).thenReturn(account);
        when(accountHistRepository.getAllByAccountId(21L)).thenReturn(List.of(history1, history2));

        List<AccountHistResult> result = accountService.retrieveAccountHist(21L);

        assertEquals(2, result.size());
        assertEquals(500L, result.get(0).balance());
    }

    @Test
    @DisplayName("히스토리 저장 시 입력값 기준으로 AccountHistory가 저장되어야 한다")
    void saveHist_shouldCallSaveWithCorrectData() {
        User user = User.builder().id(21L).name("홍길동").email("test@hhplus.kr").build();
        Account account = Account.builder().id(21L).user(user).balance(1000L).sysCretDt(LocalDateTime.now()).build();
        AccountInfo info = new AccountInfo(21L, 300L);
        AccountHistType type = AccountHistType.CHARGE;

        when(accountRepository.findByUserId(21L)).thenReturn(account);

        accountService.saveHist(info, type);

        ArgumentCaptor<AccountHistory> captor = ArgumentCaptor.forClass(AccountHistory.class);
        verify(accountHistRepository).save(captor.capture());

        AccountHistory saved = captor.getValue();
        assertEquals(21L, saved.getAccount().getId());
        assertEquals(AccountHistType.CHARGE, saved.getStatus());
        assertEquals(300L, saved.getAmount());
    }

    @Test
    @DisplayName("잔액 충전 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void chargeAmount_shouldThrowException_whenAccountNotFound() {
        AccountInfo info = new AccountInfo(999999L, 1000L);
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        CustomException ex = assertThrows(CustomException.class, () -> accountService.chargeAmount(info));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 사용 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void useAmount_shouldThrowException_whenAccountNotFound() {
        AccountInfo info = new AccountInfo(999999L, 500L);
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        CustomException ex = assertThrows(CustomException.class, () -> accountService.useAmount(info));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 조회 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void retrieveAccount_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        CustomException ex = assertThrows(CustomException.class, () -> accountService.retrieveAccount(999999L));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 이력 조회 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void retrieveAccountHist_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        CustomException ex = assertThrows(CustomException.class, () -> accountService.retrieveAccountHist(999999L));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }
}
