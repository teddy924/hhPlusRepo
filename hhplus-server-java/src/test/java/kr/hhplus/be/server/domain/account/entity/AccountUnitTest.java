package kr.hhplus.be.server.domain.account.entity;

import kr.hhplus.be.server.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountUnitTest {

    @Test
    @DisplayName("100원 단위로 충전 시 잔액이 증가")
    void charge_shouldIncreaseBalance() throws Exception {
        Account account = Account.builder().balance(1000L).build();

        account.charge(500L);

        assertEquals(1500L, account.getBalance());
    }

    @Test
    @DisplayName("100원 미만 단위로 충전 시 예외 발생")
    void charge_shouldThrow_whenInvalidUnit() {
        Account account = Account.builder().balance(1000L).build();

        CustomException ex = assertThrows(CustomException.class, () -> account.charge(333L));
        assertTrue(ex.getMessage().contains("충전/사용 금액은 100원 단위의 100원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("사용 시 잔액이 감소")
    void use_shouldDecreaseBalance() throws Exception {
        Account account = Account.builder().balance(1000L).build();

        account.use(300L);

        assertEquals(700L, account.getBalance());
    }

    @Test
    @DisplayName("100원 미만 단위로 사용 시 예외 발생")
    void use_shouldThrow_whenInvalidUnit() {
        Account account = Account.builder().balance(1000L).build();

        CustomException ex = assertThrows(CustomException.class, () -> account.use(135L));
        assertTrue(ex.getMessage().contains("충전/사용 금액은 100원 단위의 100원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("사용 금액이 잔액보다 많으면 canUse는 false를 리턴")
    void canUse_shouldReturnFalse_whenInsufficientBalance() {
        Account account = Account.builder().balance(500L).build();

        boolean result = account.canUse(1000L);

        assertFalse(result);
    }

    @Test
    @DisplayName("사용 금액이 잔액보다 적거나 같으면 canUse는 true를 리턴")
    void canUse_shouldReturnTrue_whenBalanceSufficient() {
        Account account = Account.builder().balance(1000L).build();

        boolean result = account.canUse(1000L);

        assertTrue(result);
    }

    @Test
    @DisplayName("100원 미만으로 충전 시 예외 발생")
    void charge_shouldThrow_whenAmountBelowMinimum() {
        Account account = Account.builder().balance(1000L).build();

        CustomException ex = assertThrows(CustomException.class, () -> account.charge(99L));

        assertTrue(ex.getMessage().contains("100원 단위의 100원 이상"));
    }

    @Test
    @DisplayName("100원 미만으로 사용할 경우 예외 발생")
    void use_shouldThrow_whenAmountBelowMinimum() {
        Account account = Account.builder().balance(1000L).build();

        CustomException ex = assertThrows(CustomException.class, () -> account.use(50L));

        assertTrue(ex.getMessage().contains("100원 단위의 100원 이상"));
    }

}
