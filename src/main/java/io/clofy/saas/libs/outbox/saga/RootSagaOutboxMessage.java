package io.clofy.saas.libs.outbox.saga;

import io.clofy.saas.libs.outbox.RootOutboxMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public abstract class RootSagaOutboxMessage extends RootOutboxMessage {

    private UUID sagaId;
    private SagaStatus sagaStatus;

}
