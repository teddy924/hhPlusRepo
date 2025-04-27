package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/api/coupon")
@Tag(name = "쿠폰 API", description = "쿠폰 관련 API")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @SwaggerSuccess(responseType = CouponResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , NOT_HAS_COUPON
    })
    @Operation(summary = "쿠폰 목록 조회", description = "유저 ID가 보유하고 있는 쿠폰 목록을 조회한다.")
    public ResponseEntity<ResponseApi<List<CouponResponseDTO>>> retrieve(
            @RequestParam (value = "userId") Long userId
    ) {

        List<CouponResponseDTO> couponList = couponService.retrieveCouponList(userId);

        return ResponseEntity.ok(new ResponseApi<>(true, "보유 쿠폰 목록 조회 성공", couponList));

    }

    @PostMapping("/issueCoupon")
    @SwaggerSuccess(responseType = CouponResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_COUPON
            , INVALID_COUPON
            , DUPLICATE_ISSUE_COUPON
    })
    @Operation(summary = "쿠폰 발급", description = "유저 ID에게 쿠폰을 발급한다.")
    public ResponseEntity<ResponseApi<String>> issueCoupon(
        @RequestBody CouponIssueRequestDTO couponIssueRequestDTO
    ) {

        CouponIssueCommand couponIssueCommand = couponIssueRequestDTO.toCommand();

        couponService.issueCoupon(couponIssueCommand);

        return ResponseEntity.ok(new ResponseApi<>("쿠폰 발급 성공"));

    }

}
