package kr.hhplus.be.server.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long totalAmount;

    private String orderStatus;

    private LocalDateTime orderedDt;

    private LocalDateTime sysCretDt;

    private LocalDateTime sysChgDt;

}
