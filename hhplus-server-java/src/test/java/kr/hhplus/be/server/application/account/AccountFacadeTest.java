package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.domain.account.AccountHistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountFacadeTest {

    @InjectMocks
    private AccountFacade accountFacade;

    @Mock
    private AccountService accountService;

    @Test
    @DisplayName("잔액 충전 시 accountService의 chargeAmount 및 saveHist가 호출되어야 한다")
    void charge_shouldCallChargeAndSaveHist() throws Exception {
        // given
        AccountCommand command = new AccountCommand(1L, 1000L);
        AccountInfo info = new AccountInfo(command.userId(), command.amount());

        // when
        accountFacade.charge(command);

        // then
        verify(accountService).chargeAmount(info);
        verify(accountService).saveHist(info, AccountHistType.CHARGE);
    }

    @Test
    @DisplayName("잔액 사용 시 accountService의 useAmount 및 saveHist가 호출되어야 한다")
    void use_shouldCallUseAndSaveHist() throws Exception {
        // given
        AccountCommand command = new AccountCommand(1L, 500L);
        AccountInfo info = new AccountInfo(command.userId(), command.amount());

        // when
        accountFacade.use(command);

        // then
        verify(accountService).useAmount(info);
        verify(accountService).saveHist(info, AccountHistType.USE);
    }

}