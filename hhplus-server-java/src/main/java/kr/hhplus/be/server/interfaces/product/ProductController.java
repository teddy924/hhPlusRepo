package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/product")
@Tag(name = "상품 API", description = "상품 관련 API")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductFacade productFacade;

    @GetMapping
    @SwaggerSuccess(responseType = ProductResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_PRODUCT_CATEGORY
    })
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회한다. - 카테고리 별 조회 가능")
    public ResponseEntity<ResponseApi<List<ProductResponseDTO>>> retrieve (
            @Parameter(description = "상품 카테고리") @RequestParam(required = false) String category
    ) {

        List<ProductResult> resultList = productService.retrieveAll(category);

        return ResponseEntity.ok(new ResponseApi<>(true, "상품 목록 조회 성공", resultList.stream().map(ProductResponseDTO::from).toList()));

    }

    @GetMapping("/detail")
    @SwaggerSuccess(responseType = ProductResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_PRODUCT
    })
    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회한다.")
    public ResponseEntity<ResponseApi<ProductResponseDTO>> retrieve (
            @Parameter(description = "상품 ID", required = true) @RequestParam Long productId
    ) {

        ProductResult result = productService.retrieveDetail(productId);

        return ResponseEntity.ok(new ResponseApi<>(true, "상품 상세 조회 성공", ProductResponseDTO.from(result)));

    }

    @GetMapping("/rank")
    @SwaggerSuccess(responseType = ProductResponseDTO.class)
    @SwaggerError({

    })
    @Operation(summary = "상위 상품 목록 조회", description = "상위 상품 목록을 조회한다. - 카테고리 별 조회 가능")
    public ResponseEntity<ResponseApi<List<ProductResponseDTO>>> retrieveRank(
            @Parameter(description = "상품 카테고리") @RequestParam(required = false) String category
    ) {

        List<ProductSalesResult> resultList = productFacade.retrieveRank(category);

        return ResponseEntity.ok(new ResponseApi<>(true, "상위 상품 목록 조회 성공", resultList.stream().map(ProductResponseDTO::from).toList()));

    }
}
