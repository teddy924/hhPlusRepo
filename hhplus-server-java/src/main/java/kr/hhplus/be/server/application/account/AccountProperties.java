package kr.hhplus.be.server.application.account;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "account")
public record AccountProperties(
        int maxRetry,
        int retryWaitMs
) {
}
