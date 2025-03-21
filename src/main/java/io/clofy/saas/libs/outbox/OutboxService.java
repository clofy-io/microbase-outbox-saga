package io.clofy.saas.libs.outbox;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class OutboxService<T extends RootOutboxMessage> {

    private final OutboxRepository<T> outboxRepository;
    private final OutboxMessagePublisher<T> outboxMessagePublisher;
    private final OutboxMessageType outboxMessageType;

    public OutboxService(OutboxRepository<T> outboxRepository,
                         OutboxMessagePublisher<T> outboxMessagePublisher,
                         OutboxMessageType outboxMessageType) {
        this.outboxRepository = outboxRepository;
        this.outboxMessagePublisher = outboxMessagePublisher;
        this.outboxMessageType = outboxMessageType;
    }

    OutboxRepository<T> getOutboxRepository() {
        return outboxRepository;
    }

    OutboxMessagePublisher<T> getOutboxMessagePublisher() {
        return outboxMessagePublisher;
    }

    OutboxMessageType getOutboxMessageType() {
        return outboxMessageType;
    }

    public Optional<List<T>> getOutboxMessage(OutboxStatus outboxStatus) {
        return outboxRepository.findByTypeAndOutboxStatusAndSagaStatus(outboxMessageType.getType(), outboxStatus);
    }

    public void deleteOutboxMessage(OutboxStatus outboxStatus) {
        outboxRepository.deleteByTypeAndOutboxStatus(outboxMessageType.getType(), outboxStatus);
    }

    public void updateOutboxMessage(T outboxMessage) {
        outboxMessage.versionIncrement();
        save(outboxMessage);
    }

    public void outboxPublish(T outboxMessage) {
        outboxPublish(outboxMessage, false);
    }

    public void outboxPublish(T outboxMessage, boolean persist) {
        if (outboxMessage.getPayload() == null) {
            log.error("Payload of {} is null with id: {}", outboxMessage.getClass().getName(), outboxMessage.getId());
            throw new OutboxException("Payload of " + outboxMessage.getClass().getName() + " is null");
        }
        publishPersist(outboxMessage,persist);

    }

    private void publishPersist(T outboxMessage, boolean persist) {
        if (persist) {
            save(outboxMessage);
        }else {
            try {
                outboxMessagePublisher.publish(outboxMessage,
                        ((outboxMessageCallBack, outboxStatusCallBack) -> {
                            if (!outboxStatusCallBack.equals(OutboxStatus.COMPLETED)) {
                                save(outboxMessageCallBack);
                            } else {
                                log.debug("Published {} with outbox id: {}", outboxMessage.getClass().getName(), outboxMessage.getId());
                            }
                        })
                );
            } catch (Exception e) {
                log.error("Could not publish {} pre outbox", outboxMessage.getClass().getName(), e);
                save(outboxMessage);
            }
        }
    }

    private void save(T outboxMessage) {
        T response = outboxRepository.save(outboxMessage);
        if (response == null) {
            log.error("Could not save {} with outbox id: {}", outboxMessage.getClass().getName(), outboxMessage.getId());
            throw new OutboxException("Could not save " + outboxMessage.getClass().getName() + " with outbox id: " + outboxMessage.getId());
        }
        log.debug("{} saved with outbox id: {}", outboxMessage.getClass().getName(), outboxMessage.getId());
    }


}
