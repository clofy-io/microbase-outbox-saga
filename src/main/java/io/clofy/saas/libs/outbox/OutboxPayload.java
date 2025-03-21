package io.clofy.saas.libs.outbox;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class OutboxPayload {
    private String payload;
}
