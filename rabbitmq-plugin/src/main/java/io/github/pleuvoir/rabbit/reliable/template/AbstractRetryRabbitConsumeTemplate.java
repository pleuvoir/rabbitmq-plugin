package io.github.pleuvoir.rabbit.reliable.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import io.github.pleuvoir.rabbit.RabbitConst;

import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;

/**
 * 支持重试的消费者模板，当消费异常时会自动重试多次，直到最大重试次数为止
 * 
 * <p>
 * 重试次数取决于 rabbitmq.consumer-exception-retry.max 的配置，如果没有配置此参数，则默认为{@link RabbitConst #DEFAULT_MAX_RETRY}
 * <p>
 *
 */
public abstract class AbstractRetryRabbitConsumeTemplate {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private ReliableRabbitConsumeTemplate consumeTemplate;

	@RabbitHandler
	public void onMessage(String payload, Message message, Channel channel) {
		try {
			RabbitConsumeCallBack callBack = new RabbitConsumeCallBack() {
				@Override
				public void doInTransaction() throws Exception {
					handler(new String(message.getBody()));
				}
			};
			consumeTemplate.excute(callBack, true, message, channel);
		} catch (Throwable e) {
			exceptionHandler(message, e);
		}
	}

	protected abstract void handler(String data);

	protected void exceptionHandler(Message message, Throwable e) {
	};
	
}
