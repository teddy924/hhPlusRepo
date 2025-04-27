package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_rank_snapshot")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductRankSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private Long productId;
    private Long sellerId;
    private String productName;
    private Long price;
    private Integer stock;
    private LocalDateTime efctStDt;
    private LocalDateTime efctFnsDt;
    private Integer salesQuantity;
    private Integer ranking;
    private LocalDateTime snapshotAt;

    public static ProductRankSnapshot from(ProductSalesResult result, String category, int ranking, LocalDateTime snapshotAt) {
        return ProductRankSnapshot.builder()
                .category(category)
                .productId(result.productId())
                .sellerId(result.sellerId())
                .productName(result.productName())
                .price(result.price())
                .stock(result.stock())
                .efctStDt(result.efctStDt())
                .efctFnsDt(result.efctFnsDt())
                .salesQuantity(result.salesQuantity())
                .ranking(ranking)
                .snapshotAt(snapshotAt)
                .build();
    }
}
