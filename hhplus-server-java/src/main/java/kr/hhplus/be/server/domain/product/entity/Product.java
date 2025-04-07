package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String name;

    private Long price;

    private Integer stock;

    private ProductCategoryType category;

    private LocalDateTime efctStDt;

    private LocalDateTime efctFnsDt;

    private LocalDateTime sysCretDt;

    private LocalDateTime sysChgDt;

}
