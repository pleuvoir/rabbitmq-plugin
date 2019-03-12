package io.github.pleuvoir.rabbit.reliable;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.pleuvoir.rabbit.reliable.jdbc.RabbitMessageLog;
import io.github.pleuvoir.rabbit.utils.Generator;

public class ReliableRabbitPublishTemplate extends RabbitTemplate {

	private final static Logger LOGGER = LoggerFactory.getLogger(ReliableRabbitPublishTemplate.class);
	
	@Autowired ReliableMessageService reliableMessageService;

	public ReliableRabbitPublishTemplate(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	@PostConstruct
	void setup() {
		super.setBeforePublishPostProcessors(new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				MessageProperties messageProperties = message.getMessageProperties();
				String messageId = Generator.nextUUID();
				messageProperties.setMessageId(messageId);
				RabbitMessageLog log = new RabbitMessageLog();
				log.setId(messageId);
				log.setCreateTime(LocalDateTime.now());
				log.setStatus(RabbitMessageLog.PREPARE_TO_BROKER);
				reliableMessageService.insert(log);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("*[messageId={}] 准备发送消息到 MQ Broker", messageId);
				}
				return message;
			}
		});
	}
}
