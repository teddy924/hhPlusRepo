package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long couponIssueId;
    private Long discountAmount;
    private LocalDateTime usedDt;
    private LocalDateTime sysCretDt;
    private LocalDateTime sysChgDt;

}
