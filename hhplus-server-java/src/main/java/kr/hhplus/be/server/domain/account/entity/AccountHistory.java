package kr.hhplus.be.server.domain.account.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.account.AccountHistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private AccountHistType status;

    private Long amount;

    private LocalDateTime sysCretDt;

}
