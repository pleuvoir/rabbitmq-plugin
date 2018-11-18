package io.github.pleuvoir.rabbit.support.creator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.github.pleuvoir.base.kit.ApplicationContextUtil;
import io.github.pleuvoir.rabbit.support.producer.MQMessage;


/**
 * 定时队列
 * @author pleuvoir
 *
 */
public class FixedTimeQueue {

	private static Logger logger = LoggerFactory.getLogger(FixedTimeQueue.class);

	/**
	 * 队列标识，非必须
	 */
	private String requestId;

	/**
	 * 队列的过期时间，必须
	 */
	private LocalDateTime fixedTime;

	/**
	 * 获取定时队列名称
	 */
	private String queue;

	/**
	 * 交换机名称
	 */
	private String exchange;

	/**
	 * 路由键名称
	 */
	private String routingKey;
	
	/**
	 * 死信队列路由键名称，必须
	 */
	private String deadLetterRoutingKey;
	
	/**
	 * 死信队列交换机名称，必须
	 */
	private String deadLetterExchange;
	
	/**
	 * 定时队列是否创建成功
	 */
	private boolean alive;
	
	private FixedTimeQueue(LocalDateTime fixedTime) {
		this.fixedTime = fixedTime;
	}

	/**
	 * 声明一个定时队列，该队列中应当只有一条待消费的消息
	 * @param fixedTime	队列过期的时间，即消息执行的时间
	 */
	public static FixedTimeQueue create(@NonNull LocalDateTime fixedTime) {
		return new FixedTimeQueue(fixedTime);
	}

	/**
	 * 设置队列的请求标识，用于方便的区分队列
	 * @param requestId	请求标识，建议使用清晰明了的业务标识
	 */
	public FixedTimeQueue requestId(@Nullable String requestId) {
		this.requestId = requestId;
		return this;
	}
	
	/**
	 * 设置队列的死信路由键，队列过期后会按照该路由键进行投递
	 * @param deadLetterRoutingKey	死信路由键
	 */
	public FixedTimeQueue deadLetterRoutingKey(@NonNull String deadLetterRoutingKey) {
		this.deadLetterRoutingKey = deadLetterRoutingKey;
		return this;
	}
	
	/**
	 * 设置队列的死信交换机，队列过期后会按照该交换机进行投递
	 * @param deadLetterExchange	死信交换机
	 */
	public FixedTimeQueue deadLetterExchange(@NonNull String deadLetterExchange) {
		this.deadLetterExchange = deadLetterExchange;
		return this;
	}

	/**
	 * 向  rabbit 注册一个队列，队列会在到期后自动删除 <br>
	 * 注意：当设置的 {@link #fixedTime} 小于创建时刻的时间时，定时队列将不会被创建
	 */
	public FixedTimeQueue commit() {
		
		Assert.notNull(this.deadLetterExchange, "deadLetterExchange 不能为空 .");
		Assert.notNull(this.deadLetterRoutingKey, "deadLetterRoutingKey 不能为空 .");

		long delayMillis = Duration.between(LocalDateTime.now(), this.fixedTime).toMillis();
		
		String formatedDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.fixedTime);
		if (delayMillis < 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("【定时队列创建】定时时间  {} 小于当前时间，不进行队列创建操作。", formatedDateTime);
			}
			return this;
		}

		// 设置队列命名前缀
		String namePrefix = (StringUtils.isEmpty(this.requestId) ? UUID.randomUUID().toString() : this.requestId)
							.concat("-").concat(formatedDateTime);
		
		String exchangeName 	= namePrefix.concat("-fixedTime-exchange");
		String queueName	 	= namePrefix.concat("-fixedTime-queue");
		String routingKeyName 	= namePrefix.concat("-fixedTime-routingKey");

		// 当队列过期后会自动删除交换机
		Exchange exchange = ExchangeBuilder.directExchange(exchangeName).autoDelete().durable(true).build();
		Queue queue = QueueBuilder.durable(queueName)
				.withArgument("x-dead-letter-exchange", this.deadLetterExchange)
				.withArgument("x-dead-letter-routing-key", this.deadLetterRoutingKey)
				.withArgument("x-message-ttl", delayMillis) 	    // 消息过期时间
				.withArgument("x-expires", delayMillis + 5000L) 	// 队列自动删除时间，加些时间否则来不及消费
				.build();
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKeyName).noargs();

		RabbitAdmin rabbitAdmin = ApplicationContextUtil.getBean(RabbitAdmin.class);
		rabbitAdmin.declareExchange(exchange);
		rabbitAdmin.declareQueue(queue);
		rabbitAdmin.declareBinding(binding);
		
		// 设置定时队列的属性，方便调用者获取
		this.exchange = exchangeName;
		this.queue = queueName;
		this.routingKey = routingKeyName;
		this.alive = true;
		return this;
	}
	
	
	/**
	 * 如果队列创建成功则发送消息，该方法应该在 {@link #commit()} 之后调用
	 * @param msg	发送消息体
	 */
	public void sendMessageIfAlive(MQMessage msg) {
		if (isAlive()) {
			RabbitTemplate rabbitTemplate = ApplicationContextUtil.getBean(RabbitTemplate.class);
			rabbitTemplate.convertAndSend(this.exchange, this.routingKey, msg.toJSON());
		}
	}


	/**
	 * 获取队列标识，当未设置时默认为 UUID
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * 获取定时队列的执行时间，到达此时间时，消息将被投递进入死信队列
	 */
	public LocalDateTime getFixedTime() {
		return fixedTime;
	}

	/**
	 * 获取定时队列的名称
	 */
	public String getQueue() {
		return queue;
	}

	/**
	 * 获取定时队列的交换机
	 */
	public String getExchange() {
		return exchange;
	}

	/**
	 * 获取定时队列的路由键
	 */
	public String getRoutingKey() {
		return routingKey;
	}

	/**
	 * 获取指定的死信队列路由键，该键应当是在创建定时队列时设置的
	 */
	public String getDeadLetterRoutingKey() {
		return deadLetterRoutingKey;
	}

	/**
	 * 获取指定的死信队列交换机，该交换机应当是在创建定时队列时设置的
	 * @return
	 */
	public String getDeadLetterExchange() {
		return deadLetterExchange;
	}

	/**
	 * 定时队列是否创建成功 <br>
	 * 当设置的 {@link #fixedTime} 小于创建时刻的时间时，定时队列将不会被创建
	 */
	public boolean isAlive() {
		return alive;
	}

}
