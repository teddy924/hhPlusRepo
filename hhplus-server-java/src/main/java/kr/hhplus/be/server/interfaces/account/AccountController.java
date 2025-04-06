package kr.hhplus.be.server.interfaces.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/account")
@Tag(name = "잔액 API", description = "잔액 관련 API")
@RequiredArgsConstructor
public class AccountController {

    @PostMapping("/retvBalance")
    @SwaggerSuccess(responseType = AccountResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
    })
    @Operation(summary = "잔액 조회", description = "유저 ID로 현재 잔액을 조회한다.")
    public ResponseEntity<AccountResponseDTO> retvBalance(
            @RequestBody AccountRequestDTO balanceRequestDTO
    ) {
//        return ResponseEntity.ok(new BalanceResponseSTO());
        return ResponseEntity.ok(new AccountResponseDTO(1L,40000L));
    }

    @PostMapping("/chargeBalance")
    @SwaggerSuccess(responseType = AccountResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , INVALID_CHARGE_BALANCE
    })
    @Operation(summary = "잔액 충전", description = "유저 ID로 요청된 금액만큼 충전한다.")
    public ResponseEntity<AccountResponseDTO> chargeBalance(
            @RequestBody AccountRequestDTO balanceRequestDTO
    ) {
//        return ResponseEntity.ok(new BalanceResponseSTO());
        return ResponseEntity.ok(new AccountResponseDTO(1L, 40000L));
    }

}
