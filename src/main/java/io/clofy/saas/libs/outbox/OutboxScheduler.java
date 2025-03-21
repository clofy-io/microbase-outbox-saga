package io.clofy.saas.libs.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
