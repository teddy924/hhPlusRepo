package kr.hhplus.be.server.infra.outbox;

public enum OutboxStatus {
    PENDING, SENT, FAILED, FAILED_CONSUMER
}