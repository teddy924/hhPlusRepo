package kr.hhplus.be.server.application.account;


import kr.hhplus.be.server.domain.account.entity.Account;
import kr.hhplus.be.server.interfaces.account.AccountResponseDTO;

public record AccountQueryDto(
        Long userId
        , Long amount
) {

}
