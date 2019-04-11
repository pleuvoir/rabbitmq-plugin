package io.github.pleuvoir.rabbit;

import lombok.Getter;

/**
 * MQ消费异常，当业务失败时抛出
 */
public class RabbitConsumeException extends Exception {

    private static final long serialVersionUID = -3232824325306312487L;

    @Getter
    private RetryStrategy strategy;

    public RabbitConsumeException(RetryStrategy strategy, Throwable cause) {
        super(cause);
        this.strategy = strategy;
    }

}
