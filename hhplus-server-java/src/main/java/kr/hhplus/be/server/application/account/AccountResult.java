package kr.hhplus.be.server.application.account;

import lombok.Builder;

@Builder
public record AccountResult(
        Long userId
        , Long balance
) {

}
