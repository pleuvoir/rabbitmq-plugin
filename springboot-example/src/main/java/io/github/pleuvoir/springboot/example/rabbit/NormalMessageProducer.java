package io.github.pleuvoir.springboot.example.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.pleuvoir.rabbit.support.producer.MQMessageProducer;

@Component
public class NormalMessageProducer implements MQMessageProducer<MessagePayload> {

	private static Logger logger = LoggerFactory.getLogger(NormalMessageProducer.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void send(MessagePayload data) {
		logger.info("测试可靠消息流程，发送普通消息，{}", data.toJSON());
		rabbitTemplate.convertAndSend(RabbitConstants.Normal.EXCHANGE, RabbitConstants.Normal.ROUTING_KEY,
				data.toJSON());
	}

}
