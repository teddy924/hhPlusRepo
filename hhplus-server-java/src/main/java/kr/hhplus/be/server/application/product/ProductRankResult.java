package kr.hhplus.be.server.application.product;

import lombok.Builder;

@Builder
public record ProductRankResult (
        Long productId,
        int saleQuantity
) {
}
