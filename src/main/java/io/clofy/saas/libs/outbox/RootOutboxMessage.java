package io.clofy.saas.libs.outbox;

import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Setter
@Getter
public abstract class RootOutboxMessage {
    private UUID id;
    private ZonedDateTime createdAt;

    private String type;
    private String kind;
    private String payload;
    private ZonedDateTime processedAt;
    private OutboxStatus outboxStatus;
    private int version;

    public void versionIncrement() {
        version++;
    }

    public void processed(){
        processedAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
