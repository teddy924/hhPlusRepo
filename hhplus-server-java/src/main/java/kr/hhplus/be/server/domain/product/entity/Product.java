package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.INVALID_PRODUCT;
import static kr.hhplus.be.server.config.swagger.ErrorCode.OUT_OF_STOCK;


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

    public boolean isExpired() {
        boolean isExpired = false;

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(efctStDt) && now.isBefore(efctFnsDt)) {
            isExpired = true;
        }

        return isExpired;
    }

    public static void validSalesAvailability(Product product) {

        if (product.getStock() <= 0) throw new CustomException(OUT_OF_STOCK);
        if (product.isExpired()) throw new CustomException(INVALID_PRODUCT);

    }

}
