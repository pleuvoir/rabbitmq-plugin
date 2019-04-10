package io.github.pleuvoir.springboot.example.rabbit.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import io.github.pleuvoir.rabbit.reliable.template.ReliableRabbitConsumeTemplate;
import io.github.pleuvoir.springboot.example.rabbit.RabbitConstants;
import io.github.pleuvoir.springboot.example.service.LiveBeginException;
import io.github.pleuvoir.springboot.example.service.LiveNotBeginException;
import io.github.pleuvoir.springboot.example.service.PubParamService;


//配置监听的哪一个队列，同时在没有 queue和exchange的情况下会去创建并建立绑定关系
@RabbitListener(
		containerFactory = "manualRabbitListenerContainerFactory", 
		bindings = @QueueBinding(value = @Queue(RabbitConstants.Normal.QUEUE), 
		exchange = @Exchange(RabbitConstants.Normal.EXCHANGE), 
		key = RabbitConstants.Normal.ROUTING_KEY)
)

@Service
public class NormalConsumer {

	private static Logger logger = LoggerFactory.getLogger(NormalConsumer.class);

	@Autowired
	private ReliableRabbitConsumeTemplate rabbitConsumeTemplate; // 可靠消息消费模板

	@Autowired
	private PubParamService pubParamService; // 业务类

	@RabbitHandler
	public void onMessage(String payload, Message message, Channel channel) {

		try {
			rabbitConsumeTemplate.excute(() -> {
				pubParamService.saveAndUpdate9999();
			}, false, message, channel);

		} catch (Throwable e) {
			if (e instanceof LiveBeginException) {
				return;
			} else if (e instanceof LiveNotBeginException) {
				logger.info("专场未开始，进行XXX操作。。。");
			}
		}
	}
}
