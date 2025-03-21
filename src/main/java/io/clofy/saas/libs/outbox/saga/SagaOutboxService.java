package io.clofy.saas.libs.outbox.saga;


import io.clofy.saas.libs.outbox.OutboxMessagePublisher;
import io.clofy.saas.libs.outbox.OutboxMessageType;
import io.clofy.saas.libs.outbox.OutboxService;
import io.clofy.saas.libs.outbox.OutboxStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class SagaOutboxService<T extends RootSagaOutboxMessage> extends OutboxService<T> {

    private final SagaOutboxRepository<T> sagaOutboxRepository;
    private final OutboxMessageType outboxMessageType;
    private final OutboxMessagePublisher<T> outboxMessagePublisher;
    private final SagaOutboxServiceHelper<T> sagaOutboxServiceHelper;
    public SagaOutboxService(SagaOutboxRepository<T> outboxRepository,
                             OutboxMessagePublisher<T> outboxMessagePublisher,
                             OutboxMessageType outboxMessageType) {
        super(outboxRepository, outboxMessagePublisher,outboxMessageType);
        this.sagaOutboxRepository = outboxRepository;
        this.outboxMessagePublisher = outboxMessagePublisher;
        this.outboxMessageType = outboxMessageType;
        this.sagaOutboxServiceHelper = new SagaOutboxServiceHelper<>(this);
    }

    public Optional<List<T>> getOutboxMessage(OutboxStatus outboxStatus, SagaStatus... sagaStatus){
        return sagaOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(outboxMessageType.getType(),outboxStatus,sagaStatus);
    }
    public Optional<T> getOutboxMessage(UUID sagaId, SagaStatus... sagaStatus){
        return sagaOutboxRepository.findByTypeAndSagaIdAndSagaStatus(outboxMessageType.getType(),sagaId,sagaStatus);
    }
    public void deleteOutboxMessage(OutboxStatus outboxStatus, SagaStatus... sagaStatus){
        sagaOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(outboxMessageType.getType(),outboxStatus,sagaStatus);
    }

    SagaOutboxRepository<T> getSagaOutboxRepository() {
        return sagaOutboxRepository;
    }

    OutboxMessageType getOutboxMessageType() {
        return outboxMessageType;
    }

    OutboxMessagePublisher<T> getOutboxMessagePublisher() {
        return outboxMessagePublisher;
    }

    public SagaOutboxServiceHelper<T> helper() {
        return sagaOutboxServiceHelper;
    }
}
