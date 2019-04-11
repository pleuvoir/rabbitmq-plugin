package io.github.pleuvoir.rabbit.reliable.template;

import com.rabbitmq.client.Channel;
import io.github.pleuvoir.rabbit.RabbitConst;
import io.github.pleuvoir.rabbit.RabbitConsumeException;
import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;
import io.github.pleuvoir.rabbit.reliable.jdbc.JDBCExcuteWithTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支持重试的消费者模板，当消费异常时会自动重试多次，直到最大重试次数为止
 *
 * <p>
 * 重试次数取决于 rabbitmq.consumer-exception-retry.max 的配置，如果为0则不会重试。如果没有配置此参数，则默认为{@link RabbitConst #DEFAULT_MAX_RETRY}
 * <p>
 */
public abstract class AbstractRetryRabbitConsumeTemplate {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private JDBCExcuteWithTransaction reliableExcuteWithTransaction;

    @RabbitHandler
    public void onMessage(String payload, Message message, Channel channel) {
        try {
            this.excute(() -> handler(new String(message.getBody())), true, message, channel);
        } catch (Throwable e) {
            exceptionHandler(message, e);
        }
    }

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
    private void excute(RabbitConsumeCallBack callBack, boolean requeue, Message message, Channel channel) throws Throwable {

        MessageProperties messageProperties = message.getMessageProperties();
        String messageId = messageProperties.getMessageId();
        long deliveryTag = messageProperties.getDeliveryTag();

        try {
            reliableExcuteWithTransaction.actualExcute(callBack, messageId);
            channel.basicAck(deliveryTag, false);
        } catch (RabbitConsumeException e) {
            final boolean retryEnable = e.getStrategy().isEnable();
            if (requeue && retryEnable) {
                channel.basicNack(deliveryTag, false, true);
                LOGGER.info("*[messageId={}] requeue={} retryStrategy={} MQ broker消息已拒绝，并重新投递。", messageId, true, true);
            } else {
                channel.basicNack(deliveryTag, false, false);
                LOGGER.info("*[messageId={}] requeue={} retryStrategy={} MQ broker消息已拒绝。", messageId, requeue, retryEnable);
            }
            throw e;
        }
    }

    protected abstract void handler(String data);

    protected void exceptionHandler(Message message, Throwable e) {
    };


    // helper

    /**
     * 记录异常
     */
    public void logException(Message message, Throwable e) {
        LOGGER.warn("*[messageId={}] 消息处理失败，异常信息[{}]，消息内容 ：{}", message.getMessageProperties().getMessageId(),
                e.getMessage(), new String(message.getBody()), e);
    }
}
