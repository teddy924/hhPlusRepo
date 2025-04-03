package kr.hhplus.be.server.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import kr.hhplus.be.server.coupon.dto.CouponIssueRequestDTO;
import kr.hhplus.be.server.coupon.dto.CouponIssueResponseDTO;
import kr.hhplus.be.server.coupon.dto.CouponRequestDTO;
import kr.hhplus.be.server.coupon.dto.CouponResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/coupon")
@Tag(name = "쿠폰 API", description = "쿠폰 관련 API")
@RequiredArgsConstructor
public class CouponController {

    @PostMapping("/retvCouponList")
    @SwaggerSuccess(responseType = CouponResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , NOT_HAS_COUPON
    })
    @Operation(summary = "쿠폰 목록 조회", description = "유저 ID가 보유하고 있는 쿠폰 목록을 조회한다.")
    public ResponseEntity<ResponseApi<List<CouponResponseDTO>>> retvCouponList(
        @RequestBody CouponRequestDTO couponRequestDTO
    ) {
//        return ResponseEntity.ok(new ResponseApi<>(List.of()));
        return ResponseEntity.ok(new ResponseApi<>(List.of(new CouponResponseDTO(123L
                                                                                , "mock 쿠폰"
                                                                                , "RATE"
                                                                                , 15L
                                                                                , LocalDateTime.now()
                                                                                , LocalDateTime.now().plusDays(10L)))));
    }

    @PostMapping("/issueCoupon")
    @SwaggerSuccess(responseType = CouponIssueResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , NOT_EXIST_COUPON
            , INVALID_COUPON
            , DUPLICATE_ISSUE_COUPON
    })
    @Operation(summary = "쿠폰 발급", description = "유저 ID에게 쿠폰을 발급한다.")
    public ResponseEntity<ResponseApi<CouponIssueResponseDTO>> issueCoupon(
        @RequestBody CouponIssueRequestDTO couponIssueRequestDTO
    ) {
//        return ResponseEntity.ok(new ResponseApi<>(new CouponIssueResponseDTO()));
        return ResponseEntity.ok(new ResponseApi<>(new CouponIssueResponseDTO(true, 1234231L, LocalDateTime.now())));
    }

}
