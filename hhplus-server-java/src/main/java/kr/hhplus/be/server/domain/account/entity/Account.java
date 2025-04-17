package kr.hhplus.be.server.domain.account.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long balance;

    private LocalDateTime sysCretDt;

    private LocalDateTime sysChgDt;

    // 충전
    public void charge(Long amount) throws Exception {
        validateAmount(amount);
        this.balance += amount;
    }

    // 사용
    public void use(Long amount) throws Exception {
        validateAmount(amount);
        this.balance -= amount;
    }

    // 충전, 사용 최소 금액 제한
    public void validateAmount(Long amount) throws Exception {
        if (amount == null || amount < 100 || amount % 100 != 0) {
            throw new CustomException(INVALID_ACCOUNT_AMOUNT);
        }
    }

    // 잔액이 사용량보다 많을 때만 사용 가능
    public boolean canUse(Long amount) {
        if (this.balance < amount){
            throw new CustomException(INVALID_USE_AMOUNT);
        }
        return true;
    }

}
