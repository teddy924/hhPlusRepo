package kr.hhplus.be.server.infra.account;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.user.id = :userId")
    Optional<Account> findByUserIdWithLock(@Param("userId") Long userId);

    Optional<Account> findByUserId(Long userId);
}
