package io.clofy.saas.libs.outbox.saga;

public enum SagaStatus {
    STARTED, FAILED, SUCCEEDED, PROCESSING, COMPENSATING, COMPENSATED,ONLY_OUTBOX
}

