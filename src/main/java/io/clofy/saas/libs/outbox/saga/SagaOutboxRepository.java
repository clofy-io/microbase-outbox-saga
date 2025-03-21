package io.clofy.saas.libs.outbox.saga;

import io.clofy.saas.libs.outbox.OutboxRepository;
import io.clofy.saas.libs.outbox.OutboxStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SagaOutboxRepository<T extends RootSagaOutboxMessage> extends OutboxRepository<T> {

    Optional<List<T>> findByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);
    Optional<T> findByTypeAndSagaIdAndSagaStatus(String type, UUID sagaId, SagaStatus... sagaStatus);
    void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);
}
