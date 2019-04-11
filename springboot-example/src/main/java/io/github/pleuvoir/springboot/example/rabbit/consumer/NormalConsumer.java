package io.github.pleuvoir.springboot.example.rabbit.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import io.github.pleuvoir.rabbit.reliable.template.AbstractRetryRabbitConsumeTemplate;
import io.github.pleuvoir.springboot.example.rabbit.RabbitConstants;


//配置监听的哪一个队列，同时在没有 queue和exchange的情况下会去创建并建立绑定关系
@RabbitListener(
        containerFactory = "manualRabbitListenerContainerFactory",
        bindings = @QueueBinding(value = @Queue(RabbitConstants.Normal.QUEUE),
                exchange = @Exchange(RabbitConstants.Normal.EXCHANGE),
                key = RabbitConstants.Normal.ROUTING_KEY)
)

@Service
public class NormalConsumer extends AbstractRetryRabbitConsumeTemplate {

    @Override
    protected void handler(String data) {
        LOGGER.info("NormalConsumer 接受到新消息：{}", data);
    }

    @Override
    protected void exceptionHandler(Message message, Throwable e) {
    	LOGGER.error("exception occur",e);
    }
}
