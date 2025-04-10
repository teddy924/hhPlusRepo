package kr.hhplus.be.server.application.account;

import lombok.Builder;

@Builder
public record AccountCommand(
        Long userId,
        Long amount
) {

}
