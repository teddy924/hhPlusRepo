package kr.hhplus.be.server.interfaces.account;

import kr.hhplus.be.server.application.account.AccountHistResult;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountHistResponseDto {

    Long userId;
    List<AccountHistResult> accountHistoryQueryList;

    public static AccountHistResponseDto from (List<AccountHistResult> queryDtoList) {
        return new AccountHistResponseDto(builder().userId, queryDtoList);
    }

}
