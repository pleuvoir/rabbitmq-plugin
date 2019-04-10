package io.github.pleuvoir.rabbit.reliable.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;

import io.github.pleuvoir.rabbit.RabbitConsumeException;
import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;
import io.github.pleuvoir.rabbit.reliable.jdbc.JDBCExcuteWithTransaction;

public class ReliableRabbitConsumeTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReliableRabbitConsumeTemplate.class);
	
	@Autowired
	private JDBCExcuteWithTransaction reliableExcuteWithTransaction;

	/**
	 * 可靠消息处理模板
	 * <p>
	 * 正常时会确认应答 ack， 当出现业务异常或者其它不可预知的错误时，程序会向 MQ broker 发送 nack 应答，当 requeue = true 时，此消息会被重新投递到其它消费者；
	 * 切记 requeue 参数的设置取决于再次消费是否可以恢复正常，如果不能，有可能一直轮询投递。
	 * <p>
	 * 
	 * @param callBack	待执行的业务操作，此业务操作将在数据库事务中执行
	 * @param requeue	出现异常时是否重新投递该消息，使用不当会有活锁的可能
	 * @param message
	 * @param channel
	 * @throws Throwable 
	 */
	public void excute(RabbitConsumeCallBack callBack, boolean requeue, Message message, Channel channel) throws Throwable {
		
		MessageProperties messageProperties = message.getMessageProperties();
		String messageId = messageProperties.getMessageId();
		long deliveryTag = messageProperties.getDeliveryTag();
		
		try {
			reliableExcuteWithTransaction.actualExcute(callBack, messageId);
			channel.basicAck(deliveryTag, false);
		} catch (RabbitConsumeException e) {
			if (requeue) {
				channel.basicNack(deliveryTag, false, true);
				LOGGER.info("*[messageId={}] MQ broker消息已拒绝，并重新投递给其他消费者。", messageId);
			} else {
				channel.basicNack(deliveryTag, false, false);
				LOGGER.info("*[messageId={}] MQ broker消息已拒绝。", messageId);
			}
			throw e;
		}
	}

	
	// helper
	
	/**
	 * 记录异常
	 */
	public void logException(Message message, Throwable e) {
		LOGGER.warn("*[messageId={}] 消息处理失败，异常信息[{}]，消息内容 ：{}", message.getMessageProperties().getMessageId(),
				e.getMessage(), new String(message.getBody()), e);
	}
}
