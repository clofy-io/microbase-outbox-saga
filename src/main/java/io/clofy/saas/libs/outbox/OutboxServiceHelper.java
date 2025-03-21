package io.clofy.saas.libs.outbox;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class OutboxServiceHelper<T extends RootOutboxMessage> {
    private final OutboxService<T> outboxService;
    public OutboxServiceHelper(OutboxService<T> service) {
        this.outboxService = service;
    }
    public void publish() {
        Optional<List<T>> outboxMessagesResponse =
                outboxService.getOutboxMessage(OutboxStatus.STARTED);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<T> outboxMessages = outboxMessagesResponse.get();
            if (log.isDebugEnabled()) {
                log.debug("Received {} OutboxMessage of type {} with ids: {}, sending to message bus!", outboxMessages.size(),
                        outboxService.getOutboxMessageType().getType(),
                        outboxMessages.stream().map(outboxMessage ->
                                outboxMessage.getId().toString()).collect(Collectors.joining(",")));
            }
            outboxMessages.forEach(outboxMessage ->
                    outboxService.getOutboxMessagePublisher().publish(outboxMessage, this::updateOutboxStatus));
            log.debug("{} OutboxMessage of type {} sent to message bus!",outboxService.getOutboxMessageType().getType(), outboxMessages.size());
        }
    }

    private void updateOutboxStatus(T outboxMessage, OutboxStatus outboxStatus) {
        outboxMessage.setOutboxStatus(outboxStatus);
        outboxService.updateOutboxMessage(outboxMessage);
        log.debug("OutboxMessage of type {} is updated with outbox status: {}",outboxService.getOutboxMessageType().getType(), outboxStatus.name());
    }

    public void clean() {
        var outboxMessagesResponse =
                outboxService.getOutboxMessage(OutboxStatus.COMPLETED);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            var outboxMessages = outboxMessagesResponse.get();
            if (log.isDebugEnabled()) {
                log.debug("Received {} OutboxMessage of type {} for clean-up. The payloads: {}",
                        outboxMessages.size(),
                        outboxService.getOutboxMessageType().getType(),
                        outboxMessages.stream().map(RootOutboxMessage::getPayload).collect(Collectors.joining("\n")));
            }

            outboxService.deleteOutboxMessage(OutboxStatus.COMPLETED);
            log.info("{} OutboxMessage of type {} deleted!",outboxService.getOutboxMessageType().getType(), outboxMessages.size());
        }
    }
}
