package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.product.entity.Product;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

public class ProductService {

    // 상품 유효성 확인
    public void validSalesAvailability(Product product) {

        if (product.getStock() <= 0) throw new CustomException(OUT_OF_STOCK);
        if (product.isExpired()) throw new CustomException(INVALID_PRODUCT);

    }

}
