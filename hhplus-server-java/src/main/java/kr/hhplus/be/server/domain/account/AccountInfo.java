package kr.hhplus.be.server.domain.account;

import lombok.Builder;

@Builder
public record AccountInfo (
        Long userId,
        Long amount
){

}
