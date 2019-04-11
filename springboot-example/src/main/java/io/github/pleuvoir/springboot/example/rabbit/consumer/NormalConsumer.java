package io.github.pleuvoir.springboot.example.rabbit.consumer;

import io.github.pleuvoir.rabbit.reliable.template.AbstractRetryRabbitConsumeTemplate;
import io.github.pleuvoir.springboot.example.rabbit.RabbitConstants;
import io.github.pleuvoir.springboot.example.service.LiveBeginException;
import io.github.pleuvoir.springboot.example.service.LiveNotBeginException;
import io.github.pleuvoir.springboot.example.service.PubParamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//配置监听的哪一个队列，同时在没有 queue和exchange的情况下会去创建并建立绑定关系
@RabbitListener(
        containerFactory = "manualRabbitListenerContainerFactory",
        bindings = @QueueBinding(value = @Queue(RabbitConstants.Normal.QUEUE),
                exchange = @Exchange(RabbitConstants.Normal.EXCHANGE),
                key = RabbitConstants.Normal.ROUTING_KEY)
)

@Service
public class NormalConsumer extends AbstractRetryRabbitConsumeTemplate {

    private static Logger logger = LoggerFactory.getLogger(NormalConsumer.class);

    @Autowired
    private PubParamService pubParamService; // 业务类


    @Override
    protected void handler(String data) {
        LOGGER.info("NormalConsumer 接受到新消息：{}", data);
        pubParamService.saveAndUpdate9999();
    }

    @Override
    protected void exceptionHandler(Message message, Throwable e) {
        if (e instanceof LiveBeginException) {
            return;
        } else if (e instanceof LiveNotBeginException) {
            logger.info("专场未开始，进行XXX操作。。。");
        }
    }
}
