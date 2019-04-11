package io.github.pleuvoir.rabbit;

/**
 * 重试策略
 */
public enum RetryStrategy {

    ENABLE, DISABLE;

    public boolean isEnable() {
        return this.equals(ENABLE);
    }
}
