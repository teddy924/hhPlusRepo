package kr.hhplus.be.server.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import kr.hhplus.be.server.product.dto.ProductRequestDTO;
import kr.hhplus.be.server.product.dto.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/product")
@Tag(name = "상품 API", description = "상품 관련 API")
@RequiredArgsConstructor
public class ProductController {

    @PostMapping("/retvProductList")
    @SwaggerSuccess(responseType = ProductResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER

    })
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회한다. - 카테고리 별 조회 가능")
    public ResponseEntity<ResponseApi<List<ProductResponseDTO>>> retvProductList(
            @RequestBody ProductRequestDTO productRequestDTO
    ) {
//        return ResponseEntity.ok(new ResponseApi<>(List.of(new ProductResponseDTO())));
        return ResponseEntity.ok(new ResponseApi<>(List.of(new ProductResponseDTO("P00001"
                , "mock 텐트"
                , 2L
                , "TENT"
                , 25000L
                , 10))));
    }

    @PostMapping("/retvProductDetail")
    @SwaggerSuccess(responseType = ProductResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER

    })
    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회한다.")
    public ResponseEntity<ResponseApi<ProductResponseDTO>> retvProductDetail(
            @RequestBody ProductRequestDTO productRequestDTO
    ) {
//        return ResponseEntity.ok(new ResponseApi<>(new ProductResponseDTO()));
        return ResponseEntity.ok(new ResponseApi<>(
                new ProductResponseDTO("P00001"
                , "mock 텐트"
                , 2L
                , "TENT"
                , 25000L
                , 10)));
    }

    @PostMapping("/retvRankProduct")
    @SwaggerSuccess(responseType = ProductResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER

    })
    @Operation(summary = "상위 상품 목록 조회", description = "상위 상품 목록을 조회한다. - 카테고리 별 조회 가능")
    public ResponseEntity<ResponseApi<List<ProductResponseDTO>>> retvRankProduct(
            @RequestBody ProductRequestDTO productRequestDTO
    ) {
        return ResponseEntity.ok(new ResponseApi<>(List.of(
                new ProductResponseDTO("P00002", "mock A텐트", 2L, "TENT", 1000L, 3)
                , new ProductResponseDTO("P00003", "mock 3인용 텐트", 2L, "TENT", 3000L, 4)
                , new ProductResponseDTO("P00004", "mock 4인용 텐트", 3L, "TENT", 3000L, 5)
                , new ProductResponseDTO("P00005", "mock 5인용 텐트", 4L, "TENT", 3000L, 6)
                , new ProductResponseDTO("P00006", "mock 6인용 텐트", 5L, "TENT", 3000L, 7)
        )));
    }
}
