package kr.hhplus.be.server.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import kr.hhplus.be.server.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.order.dto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/order")
@Tag(name = "주문 API", description = "주문 관련 API")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping("/orderPayment")
    @SwaggerSuccess(responseType = OrderResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , NOT_HAS_COUPON
    })
    @Operation(summary = "주문 결제", description = "주문 후 결제를 진행한다.")
    public ResponseEntity<ResponseApi<OrderResponseDTO>> orderPayment(
            @RequestBody OrderRequestDTO orderRequestDTO
    ) {
//        return ResponseEntity.ok(new ResponseApi<>(new OrderResponseDTO()));
        return ResponseEntity.ok(new ResponseApi<>(new OrderResponseDTO(1001L
                , true
                , 25000L
                , 15000L
                , LocalDateTime.now()
                , "mock 결제 성공")));
    }

}
