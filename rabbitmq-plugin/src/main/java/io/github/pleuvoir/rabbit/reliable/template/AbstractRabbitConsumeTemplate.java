package io.github.pleuvoir.rabbit.reliable.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;

import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;

public abstract class AbstractRabbitConsumeTemplate {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private ReliableRabbitConsumeTemplate consumeTemplate;

	@RabbitHandler
	public void onMessage(String payload, Message message, Channel channel) {
		try {
			RabbitConsumeCallBack callBack = new RabbitConsumeCallBack() {
				@Override
				public void doInTransaction() throws Exception {
					handler(message);
				}
			};
			consumeTemplate.excute(callBack, enableExceptionRetry(), message, channel);
		} catch (Throwable e) {
			exceptionHandler(message, e);
		}
	}

	protected abstract void handler(Message message);

	protected void exceptionHandler(Message message, Throwable e) {
	};
	
	/**
	 * 出现异常时是否重试，注意：如果该异常无法恢复，可能会导致活锁，请合理设置重试次数
	 * 
	 */
	protected abstract boolean enableExceptionRetry();
}
