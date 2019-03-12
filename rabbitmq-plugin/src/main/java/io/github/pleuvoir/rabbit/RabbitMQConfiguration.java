package io.github.pleuvoir.rabbit;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;

import io.github.pleuvoir.base.kit.ApplicationContextUtil;
import io.github.pleuvoir.base.kit.PropertiesLoadUtil;
import io.github.pleuvoir.base.kit.PropertiesWrap;
import io.github.pleuvoir.rabbit.reliable.ReliableMessageService;
import io.github.pleuvoir.rabbit.reliable.ReliableRabbitConsumeTemplate;
import io.github.pleuvoir.rabbit.reliable.ReliableRabbitPublishTemplate;
import io.github.pleuvoir.rabbit.reliable.jdbc.JDBCReliableMessageService;
import io.github.pleuvoir.rabbit.support.creator.FixedTimeQueueHelper;

@Import({ApplicationContextUtil.class})
@EnableRabbit
public class RabbitMQConfiguration extends DataSourceConfiguration {


	public RabbitMQConfiguration() {
		LOGGER.info("rabbitmq-plugin RabbitMQConfiguration loading ...");
	}

	private String rabbitmqHost;
	private Integer rabbitmqPort;
	private String rabbitmqVirtualHost;
	private String rabbitmqUsername;
	private String rabbitmqPassword;

	/**
	 * 设置RabbitMQ配置
	 */
	public void setLocation(String location) throws IOException {
		PropertiesWrap config = PropertiesLoadUtil.pathToProWrap(location);
		rabbitmqHost = config.getString("rabbitmq.host");
		Assert.notNull(rabbitmqHost, "rabbitmqHost must be non-null.");
		rabbitmqPort = config.getInteger("rabbitmq.port", 5672);
		rabbitmqVirtualHost = config.getString("rabbitmq.virtualHost", "/");
		rabbitmqUsername = config.getString("rabbitmq.username");
		rabbitmqPassword = config.getString("rabbitmq.password");
	}

	/**
	 * Broker连接工厂
	 */
	@Bean(name = "connectionFactory")
	public ConnectionFactory getConnectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(rabbitmqHost);
		factory.setPort(this.rabbitmqPort == null ? 5672 : rabbitmqPort);
		factory.setVirtualHost(StringUtils.isBlank(rabbitmqVirtualHost) ? "/" : rabbitmqVirtualHost);
		if (StringUtils.isNotBlank(rabbitmqUsername)) {
			factory.setUsername(rabbitmqUsername);
		}
		if (StringUtils.isNotBlank(rabbitmqPassword)) {
			factory.setPassword(rabbitmqPassword);
		}
		return factory;
	}
	

	// helper
	@Bean(name = "rabbitAdmin")
	public RabbitAdmin getRabbitAdmin(RabbitTemplate rabbitTemplate) {
		return new RabbitAdmin(rabbitTemplate);
	}

	/**
	 * 发布消息使用的模版，因而发送方的许多参数可以在这里设置<br> 
	 * 每一个消息情况不同，有的需要回调有的不需要，使用同一个会报错，故使用多例，如果项目采用手动确认则必须设置为多例
	 */
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean
	public RabbitTemplate getRabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	// 可靠消息发送模板 ，每次发送都会在日志表中记录，优先使用
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(name = "reliableRabbitTemplate")
	public ReliableRabbitPublishTemplate reliableRabbitTemplate(ConnectionFactory connectionFactory) {
		return new ReliableRabbitPublishTemplate(connectionFactory);
	}

	// 可靠消息消费模板
	@Bean(name = "reliableRabbitConsumeTemplate")
	public ReliableRabbitConsumeTemplate reliableRabbitConsumeTemplate() {
		return new ReliableRabbitConsumeTemplate();
	}

	// 可靠消息数据库日志支持
	@Bean(name = "jdbcReliableMessageService")
	public ReliableMessageService reliableMessageService() {
		return new JDBCReliableMessageService();
	}

	// 定时队列支持
	@Bean
	public FixedTimeQueueHelper fixedTimeQueueHelper(RabbitAdmin rabbitAdmin) {
		return new FixedTimeQueueHelper(rabbitAdmin);
	}
}
