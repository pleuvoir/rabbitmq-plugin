package io.github.pleuvoir.rabbit;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import io.github.pleuvoir.base.kit.ApplicationContextUtil;
import io.github.pleuvoir.base.kit.PropertiesLoadUtil;
import io.github.pleuvoir.base.kit.PropertiesWrap;

@Import({ ApplicationContextUtil.class })
@EnableRabbit
public class RabbitMQConfiguration {

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

	@Bean(name = "rabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory getRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMaxConcurrentConsumers(20);
		factory.setAcknowledgeMode(AcknowledgeMode.NONE);
		return factory;
	}

	@Bean(name = "rabbitTemplate")
	public RabbitTemplate getRabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	@Bean(name = "rabbitAdmin")
	public RabbitAdmin getRabbitAdmin(RabbitTemplate rabbitTemplate) {
		return new RabbitAdmin(rabbitTemplate);
	}

}
