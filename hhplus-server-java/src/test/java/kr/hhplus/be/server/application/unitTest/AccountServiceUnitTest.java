package kr.hhplus.be.server.application.unitTest;

import kr.hhplus.be.server.application.account.AccountHistResult;
import kr.hhplus.be.server.application.account.AccountResult;
import kr.hhplus.be.server.application.account.AccountService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.redis.RedisSlaveSelector;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;
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

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private RedisSlaveSelector redisSlaveSelector;

    @Test
    @DisplayName("잔액 충전 시 계좌에서 charge 호출 후 저장되어야 한다")
    void chargeAmount_shouldIncreaseBalanceAndSave() throws Exception {
        // given
        AccountInfo info = new AccountInfo(21L, 1000L);
        Account mockAccount = mock(Account.class);
        when(accountRepository.findByUserId(21L)).thenReturn(mockAccount);

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
        AccountInfo info = new AccountInfo(21L, 500L);
        Account mockAccount = mock(Account.class);
        when(accountRepository.findByUserId(21L)).thenReturn(mockAccount);
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
        User user = User.builder()
                .id(21L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();
        Account account = Account.builder()
                .id(21L)
                .user(user)
                .balance(5000L)
                .sysCretDt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUserId(21L)).thenReturn(account);

        // when
        AccountResult result = accountService.retrieveAccount(21L);

        // then
        assertEquals(21L, result.userId());
        assertEquals(5000L, result.balance());
    }

    @Test
    @DisplayName("잔액 이력 조회 시 유저 기준으로 히스토리 리스트가 반환되어야 한다")
    void retrieveAccountHist_shouldReturnHistoryList() throws Exception {
        // given
        User user = User.builder()
                .id(21L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();
        Account account = Account.builder()
                .id(21L)
                .user(user)
                .balance(1000L)
                .sysCretDt(LocalDateTime.now())
                .build();
        AccountHistory history1 = AccountHistory.builder()
                .id(21L)
                .account(account)
                .status(AccountHistType.CHARGE)
                .amount(1000L)
                .sysCretDt(LocalDateTime.now().minusHours(1L))
                .build();
        AccountHistory history2 = AccountHistory.builder()
                .id(22L)
                .account(account)
                .status(AccountHistType.USE)
                .amount(500L)
                .sysCretDt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUserId(21L)).thenReturn(account);
        when(accountHistRepository.getAllByAccountId(21L)).thenReturn(List.of(history1, history2));

        // when
        List<AccountHistResult> result = accountService.retrieveAccountHist(21L);

        // then
        assertEquals(2, result.size());
        assertEquals(500L, result.get(0).balance());
    }

    @Test
    @DisplayName("히스토리 저장 시 입력값 기준으로 AccountHistory가 저장되어야 한다")
    void saveHist_shouldCallSaveWithCorrectData() {
        // given
        User user = User.builder()
                .id(21L)
                .name("홍길동")
                .email("test@hhplus.kr")
                .build();
        Account account = Account.builder()
                .id(21L)
                .user(user)
                .balance(1000L)
                .sysCretDt(LocalDateTime.now())
                .build();
        AccountInfo info = new AccountInfo(21L, 300L);
        AccountHistType type = AccountHistType.CHARGE;

        when(accountRepository.findByUserId(21L)).thenReturn(account);

        // when
        accountService.saveHist(info, type);

        // then
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
        // given
        AccountInfo info = new AccountInfo(999999L, 1000L);
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.chargeAmount(info));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 사용 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void useAmount_shouldThrowException_whenAccountNotFound() {
        // given
        AccountInfo info = new AccountInfo(999999L, 500L);
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.useAmount(info));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 조회 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void retrieveAccount_shouldThrowException_whenAccountNotFound() {
        // given
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.retrieveAccount(999999L));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("잔액 이력 조회 시 존재하지 않는 계좌일 경우 예외가 발생해야 한다")
    void retrieveAccountHist_shouldThrowException_whenAccountNotFound() {
        // given
        when(accountRepository.findByUserId(999999L)).thenThrow(new CustomException(NOT_EXIST_ACCOUNT));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> accountService.retrieveAccountHist(999999L));
        assertTrue(ex.getMessage().contains("계좌가 존재하지 않습니다."));
    }

    @BeforeEach
    void setUp() {
        // Redis 관련 객체들 Mocking 후 미호출 시 Unnecessary stubbings 해결을 위해 lenient() 사용
        // RedisTemplate stubbing
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        // RedisSlaveSelector stubbing
        lenient().when(redisSlaveSelector.getRandomSlave()).thenReturn(redisTemplate);
    }
}
