package kr.hhplus.be.server.interfaces.account;

import kr.hhplus.be.server.application.account.AccountQueryDto;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long id;
    private Long amount;

    public static AccountResponseDTO from(AccountQueryDto queryDto) {
        return new AccountResponseDTO(queryDto.userId(), queryDto.amount());
    }
}
