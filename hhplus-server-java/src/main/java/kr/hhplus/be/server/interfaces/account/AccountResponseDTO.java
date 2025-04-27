package kr.hhplus.be.server.interfaces.account;

import kr.hhplus.be.server.application.account.AccountResult;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long id;
    private Long balance;

    public static AccountResponseDTO from(AccountResult queryDto) {
        return new AccountResponseDTO(queryDto.userId(), queryDto.balance());
    }
}
