package kr.hhplus.be.server.domain.account;

import kr.hhplus.be.server.domain.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void chargeAmount (Account account, Long amount) throws Exception {

        account.charge(amount);

        accountRepository.save(account);
    }

    public void useAmount (Account account, Long amount) throws Exception {

        if (account.canUse(amount)) {
            account.use(amount);
        }

        accountRepository.save(account);

    }

}
