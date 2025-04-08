package kr.hhplus.be.server.application.account;

public record AccountCommand(
        Long userId,
        Long amount
) {

}
