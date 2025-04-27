package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.OrderCancelCommand;
import kr.hhplus.be.server.application.order.OrderCommand;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderService;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/api/order")
@Tag(name = "주문 API", description = "주문 관련 API")
@RequiredArgsConstructor
public class OrderController {

    public final OrderFacade orderFacade;
    public final OrderService orderService;

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

    @GetMapping("/orders/search")
    @SwaggerSuccess(responseType = OrderResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_ORDER
    })
    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회한다.")
    public ResponseEntity<ResponseApi<List<OrderResponseDTO>>> retrieve(
            @RequestParam (value = "userId") Long userId
    ) {

        List<OrderResponseDTO> orders = orderService.retrieveOrdersByUserId(userId);

        return ResponseEntity.ok(new ResponseApi<>(true, "유저 주문 목록 조회 성공", orders));
    }

    @GetMapping("/orders/detail")
    @SwaggerSuccess(responseType = OrderResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_ORDER
    })
    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보를 조회한다.")
    public ResponseEntity<ResponseApi<OrderDetailResponseDTO>> retrieveDetail(
            @RequestParam (value = "orderId") Long orderId
    ) {
        OrderDetailResponseDTO detail = orderFacade.retrieveOrderDetail(orderId);
        return ResponseEntity.ok(new ResponseApi<>(true, "주문 상세 조회 성공", detail));
    }

}
