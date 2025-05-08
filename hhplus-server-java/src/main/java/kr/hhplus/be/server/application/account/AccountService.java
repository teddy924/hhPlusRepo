package kr.hhplus.be.server.application.account;

import kr.hhplus.be.server.config.redis.RedisSlaveSelector;
import kr.hhplus.be.server.domain.account.AccountHistRepository;
import kr.hhplus.be.server.domain.account.AccountHistType;
import kr.hhplus.be.server.domain.account.AccountInfo;
import kr.hhplus.be.server.domain.account.AccountRepository;
import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHistRepository accountHistRepository;
    private final RedisSlaveSelector redisSlaveSelector;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AccountService(
            AccountRepository accountRepository
            , AccountHistRepository accountHistRepository
            , RedisSlaveSelector redisSlaveSelector
            , @Qualifier("masterRedisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        this.accountRepository = accountRepository;
        this.accountHistRepository = accountHistRepository;
        this.redisSlaveSelector = redisSlaveSelector;
        this.redisTemplate = redisTemplate;
    }

    // 잔액 충전
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void chargeAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.findByUserId(info.userId());

        account.charge(info.amount());

        accountRepository.save(account);

        saveHist(info, AccountHistType.CHARGE);

        String cacheKey = "account:" + info.userId();
        redisTemplate.opsForValue().set(cacheKey,account.getBalance().toString());
    }

    // 잔액 사용
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void useAmount (AccountInfo info) throws Exception {

        Account account = accountRepository.findByUserId(info.userId());

        if (account.canUse(info.amount())) {
            account.use(info.amount());
        }

        accountRepository.save(account);

        saveHist(info, AccountHistType.USE);

        String cacheKey = "account:" + info.userId();
        redisTemplate.opsForValue().set(cacheKey,account.getBalance().toString());

    }

    // 잔액 조회
    @Transactional
    public AccountResult retrieveAccount(Long userId) throws Exception {
        RedisTemplate<String, String> slaveRedis = redisSlaveSelector.getRandomSlave();

        String cacheKey = "account:" + userId;
        Account account = accountRepository.findByUserId(userId);

        slaveRedis.opsForValue().set(cacheKey,account.getBalance().toString());

        return new AccountResult(account.getUser().getId(), account.getBalance());

    }

    // 잔액 변동 이력 조회
    @Transactional
    public List<AccountHistResult> retrieveAccountHist(Long userId) throws Exception {

        Account account = accountRepository.findByUserId(userId);

        List<AccountHistory> histories = accountHistRepository.getAllByAccountId(account.getId())
                .stream()
                .sorted(Comparator.comparing(AccountHistory::getSysCretDt).reversed())
                .toList();

        return histories.stream()
                .map(AccountHistResult::from)
                .toList();


    }

    // 잔액 변동 이력 저장
    @Transactional
    public void saveHist(AccountInfo info, AccountHistType histType) {

        Account account = accountRepository.findByUserId(info.userId());

        AccountHistory history = AccountHistory.builder()
                .account(account) // 연관된 Account 엔티티 직접 설정
                .status(histType)
                .amount(info.amount())
                .sysCretDt(LocalDateTime.now())
                .build();

        accountHistRepository.save(history);

    }

}
