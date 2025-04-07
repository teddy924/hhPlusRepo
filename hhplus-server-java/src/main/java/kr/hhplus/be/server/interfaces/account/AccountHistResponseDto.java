package kr.hhplus.be.server.interfaces.account;

import kr.hhplus.be.server.application.account.AccountHistQueryDto;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountHistResponseDto {

    Long userId;
    List<AccountHistQueryDto> accountHistoryQueryList;

    public static AccountHistResponseDto from (List<AccountHistQueryDto> queryDtoList) {
        return new AccountHistResponseDto(builder().userId, queryDtoList);
    }

}
