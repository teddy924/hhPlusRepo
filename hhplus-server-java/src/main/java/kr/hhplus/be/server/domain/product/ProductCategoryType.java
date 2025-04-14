package kr.hhplus.be.server.domain.product;

import lombok.Getter;

@Getter
public enum ProductCategoryType {

    TENT("텐트")
    , TARP("타프")
    , FURNITURE("가구류")
    , BEDDING("침구류")
    , KITCHENWARE("주방용품")
    , ACC("악세서리")
    ;

    private final String description;

    ProductCategoryType(String description) {
        this.description = description;
    }

}
