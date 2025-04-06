package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private int amount;

    private String paymentMethod;

    private String paymentStatus;

    private LocalDateTime paidDt;

    private LocalDateTime sysCretDt;

    private LocalDateTime sysChgDt;

}
