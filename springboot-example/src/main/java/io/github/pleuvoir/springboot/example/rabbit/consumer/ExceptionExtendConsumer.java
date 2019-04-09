package io.github.pleuvoir.springboot.example.rabbit.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.pleuvoir.rabbit.reliable.template.AbstractRabbitConsumeTemplate;
import io.github.pleuvoir.springboot.example.rabbit.RabbitConstants;
import io.github.pleuvoir.springboot.example.service.PubParamService;


//配置监听的哪一个队列，同时在没有 queue和exchange的情况下会去创建并建立绑定关系
@RabbitListener(containerFactory = "manualRabbitListenerContainerFactory", bindings = @QueueBinding(value = @Queue(RabbitConstants.Exception.QUEUE), exchange = @Exchange(RabbitConstants.Exception.EXCHANGE), key = RabbitConstants.Exception.ROUTING_KEY))

@Service
public class ExceptionExtendConsumer extends AbstractRabbitConsumeTemplate {


	@Autowired
	private PubParamService pubParamService; // 业务类


	@Override
	protected void handler(Message message) {
		pubParamService.saveAndUpdate9999WithException();
	}

	@Override
	protected boolean enableExceptionRetry() {
		return Boolean.TRUE;
	}

	@Override
	protected void exceptionHandler(Message message, Throwable e) {
		LOGGER.error("我错了。。。");
	}

}
