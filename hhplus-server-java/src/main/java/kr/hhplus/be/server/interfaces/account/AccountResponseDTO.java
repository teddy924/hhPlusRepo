package kr.hhplus.be.server.interfaces.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long id;
    private Long balance;

}
