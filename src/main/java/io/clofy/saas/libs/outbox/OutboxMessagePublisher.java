package io.clofy.saas.libs.outbox;

import java.util.function.BiConsumer;

public interface OutboxMessagePublisher<T extends RootOutboxMessage> {
    default void publish(T outboxMessage, BiConsumer<T, OutboxStatus> outboxCallback){
        throw new UnsupportedOperationException("Publishing outbox messages is not implemented");
    }
}
