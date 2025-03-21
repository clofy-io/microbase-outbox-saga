package io.clofy.saas.libs.outbox;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository<T extends RootOutboxMessage> {
    T save(T outboxMessage);
    Optional<List<T>> findByTypeAndOutboxStatusAndSagaStatus(String type,OutboxStatus outboxStatus);
    Optional<T> findByType(String type);
    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
