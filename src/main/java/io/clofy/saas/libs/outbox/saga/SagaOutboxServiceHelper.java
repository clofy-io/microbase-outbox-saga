package io.clofy.saas.libs.outbox.saga;

import io.clofy.saas.libs.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class SagaOutboxServiceHelper<T extends RootSagaOutboxMessage> {

    private final SagaOutboxService<T> sagaOutboxService;

    public SagaOutboxServiceHelper(final SagaOutboxService<T> sagaOutboxService) {
        this.sagaOutboxService = sagaOutboxService;
    }

    public void publish() {
        Optional<List<T>> outboxMessagesResponse =
                sagaOutboxService.getOutboxMessage(OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING,SagaStatus.ONLY_OUTBOX);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<T> outboxMessages = outboxMessagesResponse.get();
            if (log.isDebugEnabled()) {
                log.debug("Received {} SagaOutboxMessage of type {} with ids: {}, sending to message bus!",
                        outboxMessages.size(),
                        sagaOutboxService.getOutboxMessageType().getType(),
                        outboxMessages.stream().map(outboxMessage ->
                                outboxMessage.getId().toString()).collect(Collectors.joining(",")));
            }
            outboxMessages.forEach(outboxMessage ->
                    sagaOutboxService.getOutboxMessagePublisher().publish(outboxMessage, this::updateOutboxStatus));
            log.debug("{} SagaOutboxMessage of type {} sent to message bus!", sagaOutboxService.getOutboxMessageType().getType(),outboxMessages.size());
        }
    }

    private void updateOutboxStatus(T outboxMessage, OutboxStatus outboxStatus) {
        outboxMessage.setOutboxStatus(outboxStatus);
        sagaOutboxService.updateOutboxMessage(outboxMessage);
        log.debug("SagaOutboxMessage of type {} is updated with outbox status: {}",sagaOutboxService.getOutboxMessageType().getType(), outboxStatus.name());
    }

    public void clean() {
        var outboxMessagesResponse =
                sagaOutboxService.getOutboxMessage(
                        OutboxStatus.COMPLETED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED,
                        SagaStatus.COMPENSATED,
                        SagaStatus.ONLY_OUTBOX
                );

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            var outboxMessages = outboxMessagesResponse.get();
            if (log.isDebugEnabled()) {
                log.debug("Received {} SagaOutboxMessage of type {} for clean-up. The payloads: {}",
                        outboxMessages.size(),
                        sagaOutboxService.getOutboxMessageType().getType(),
                        outboxMessages.stream().map(RootSagaOutboxMessage::getPayload).collect(Collectors.joining("\n")));
            }

            sagaOutboxService.deleteOutboxMessage(OutboxStatus.COMPLETED,
                    SagaStatus.SUCCEEDED,
                    SagaStatus.FAILED,
                    SagaStatus.COMPENSATED,
                    SagaStatus.ONLY_OUTBOX);
            log.info("{} SagaOutboxMessage of type {} deleted!",sagaOutboxService.getOutboxMessageType(), outboxMessages.size());
        }
    }
}
