package io.github.pleuvoir.rabbit;

/**
 * MQ消费异常，当业务失败时抛出
 *
 */
public class RabbitConsumeException extends Exception {

	private static final long serialVersionUID = -3232824325306312487L;

	public RabbitConsumeException(Throwable cause) {
		super(cause);
	}

}
