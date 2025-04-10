package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.OrderCancelCommand;
import kr.hhplus.be.server.application.order.OrderCommand;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/order")
@Tag(name = "주문 API", description = "주문 관련 API")
@RequiredArgsConstructor
public class OrderController {

    public final OrderFacade orderFacade;

    @PostMapping("/order")
    @SwaggerSuccess(responseType = OrderResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , NOT_HAS_COUPON
            , NOT_EXIST_COUPON
            , NOT_EXIST_ORDER
    })
    @Operation(summary = "주문 결제", description = "주문 후 결제를 진행한다.")
    public ResponseEntity<ResponseApi<String>> order(
            @RequestBody OrderRequestDTO orderRequestDTO
    ) throws Exception {

        OrderCommand command = orderRequestDTO.toCommand();

        orderFacade.order(command);

        return new ResponseEntity<>(new ResponseApi<>("주문 결제 성공"), HttpStatus.OK);

    }

    @PostMapping("/cancel")
    @SwaggerSuccess(responseType = OrderResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_ORDER
    })
    @Operation(summary = "주문 취소", description = "주문을 취소한다.")
    public ResponseEntity<ResponseApi<String>> cancel(
            @RequestBody OrderCancelRequestDTO orderCancelRequestDTO
    ) throws Exception {

        OrderCancelCommand command = orderCancelRequestDTO.toCommand();

        orderFacade.cancel(command);

        return new ResponseEntity<>(new ResponseApi<>("주문 취소 성공"), HttpStatus.OK);

    }

}
