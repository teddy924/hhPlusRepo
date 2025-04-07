package kr.hhplus.be.server.domain.account.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long balnace;

    @CreatedDate
    private LocalDateTime sysCretDt;

    @LastModifiedDate
    private LocalDateTime sysChgDt;

    // 충전
    public void charge(Long amount) throws Exception {
        validateAmount(amount);
        this.balnace += amount;
    }

    // 사용
    public void use(Long amount) throws Exception {
        validateAmount(amount);
        this.balnace -= amount;
    }

    // 충전, 사용 최소 금액 제한
    public void validateAmount(Long amount) throws Exception {
        if (amount % 100 != 0) {
            throw new Exception("금액은 100원 단위 이상으로만 가능합니다.");
        }
    }

    // 잔액이 사용량보다 많을 때만 사용 가능
    public boolean canUse(Long amount) {
        return this.balnace >= amount;
    }

}
