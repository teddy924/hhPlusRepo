package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String receiverName;
    private String phone;
    private String address1;
    private String address2;
    private String zipcode;
    private String memo;
    private LocalDateTime sysCretDt;
    private LocalDateTime sysChgDt;

}
