package kr.hhplus.be.server.application.account;

import lombok.Builder;

@Builder
public record AccountInfo (
        Long userId,
        Long amount
){

}
