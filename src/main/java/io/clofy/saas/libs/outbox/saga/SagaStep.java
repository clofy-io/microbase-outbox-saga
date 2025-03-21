package io.clofy.saas.libs.outbox.saga;

public interface SagaStep<T> {
    void process(T data);
    void rollback(T data);
}

