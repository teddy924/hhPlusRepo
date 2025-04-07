package kr.hhplus.be.server.interfaces.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.account.*;
import kr.hhplus.be.server.common.ResponseApi;
import kr.hhplus.be.server.config.swagger.SwaggerError;
import kr.hhplus.be.server.config.swagger.SwaggerSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@RestController
@RequestMapping("/account")
@Tag(name = "잔액 API", description = "잔액 관련 API")
@RequiredArgsConstructor
public class AccountController {

    private final AccountQueryService accountQueryService;
    private final AccountFacade accountFacade;
    private final AccountCommandService accountCommandService;

    @GetMapping("/{userId}/balance")
    @SwaggerSuccess(responseType = AccountResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
    })
    @Operation(summary = "잔액 조회", description = "유저 ID로 현재 잔액을 조회한다.")
    public ResponseEntity<ResponseApi<AccountResponseDTO>> retrieve (
            @PathVariable Long userId
    ) throws Exception {

        AccountQueryDto queryDto = accountQueryService.retrieveAccount(userId);

        AccountResponseDTO responseDto = AccountResponseDTO.from(queryDto);

        return ResponseEntity.ok(new ResponseApi<>(true, "잔액 조회 성공", responseDto));
    }

    @GetMapping("/{userId}/balance/history")
    @SwaggerSuccess(responseType = AccountResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
    })
    @Operation(summary = "잔액 변동 이력 조회", description = "유저 ID로 잔액 변동 이력을 조회한다.")
    public ResponseEntity<ResponseApi<AccountHistResponseDto>> retrieveHistory (
            @PathVariable Long userId
    ) throws Exception {

        List<AccountHistQueryDto> queryDtoList = accountQueryService.retrieveAccountHist(userId);

        AccountHistResponseDto responseDto = AccountHistResponseDto.from(queryDtoList);

        return ResponseEntity.ok(new ResponseApi<>(true, "잔액 변동 이력 조회 성공", responseDto));
    }

    @PostMapping("/charge")
    @SwaggerSuccess(responseType = AccountResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
            , INVALID_CHARGE_BALANCE
    })
    @Operation(summary = "잔액 충전", description = "유저 ID로 보유한 잔액에 요청된 금액만큼 충전한다.")
    public ResponseEntity<ResponseApi<String>> charge (
            @RequestBody AccountRequestDTO accountRequestDTO
    ) throws Exception {

        accountFacade.charge(accountRequestDTO);

        return ResponseEntity.ok(new ResponseApi<>("충전 완료"));
    }

    @PostMapping("/use")
    @SwaggerSuccess(responseType = AccountResponseDTO.class)
    @SwaggerError({
            NOT_EXIST_USER
    })
    @Operation(summary = "잔액 사용", description = "유저 ID가 보유한 잔액 중 요청된 금액을 사용한다.")
    public ResponseEntity<ResponseApi<String>> use (
            @RequestBody AccountRequestDTO accountRequestDTO
    ) throws Exception {

        accountFacade.use(accountRequestDTO);

        return ResponseEntity.ok(new ResponseApi<>("사용 완료"));
    }



}
