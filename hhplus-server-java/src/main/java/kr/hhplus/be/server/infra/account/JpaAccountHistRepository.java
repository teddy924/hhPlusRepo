package kr.hhplus.be.server.infra.account;

import kr.hhplus.be.server.domain.account.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAccountHistRepository extends JpaRepository<AccountHistory, Long> {

    List<AccountHistory> findAllByAccountId(Long AccountId);

}
