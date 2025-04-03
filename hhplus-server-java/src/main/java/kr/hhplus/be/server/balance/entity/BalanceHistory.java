package kr.hhplus.be.server.balance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "balance_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long balanceId;
    private String status;
    private Long amount;
    private LocalDateTime sysCretDt;

}
