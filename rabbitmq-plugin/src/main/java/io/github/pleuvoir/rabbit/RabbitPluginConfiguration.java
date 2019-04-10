package io.github.pleuvoir.rabbit;

import java.io.IOException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.pleuvoir.base.kit.PropertiesLoadUtil;
import io.github.pleuvoir.base.kit.PropertiesWrap;
import io.github.pleuvoir.rabbit.extension.FixedTimeQueueHelper;
import io.github.pleuvoir.rabbit.reliable.template.PublishTemplateConfig;
import io.github.pleuvoir.rabbit.reliable.template.PublishTemplateConfig.PublishTemplateConfigBuilder;
import io.github.pleuvoir.rabbit.reliable.template.ReliableRabbitConsumeTemplate;
import io.github.pleuvoir.rabbit.reliable.template.ReliableRabbitPublishTemplate;

@EnableRabbit
@ComponentScan({"io.github.pleuvoir.rabbit.reliable"})
public class RabbitPluginConfiguration {

	static final Logger LOGGER = LoggerFactory.getLogger(RabbitPluginConfiguration.class);

	final PublishTemplateConfigBuilder publishTemplateConfigBuilder = PublishTemplateConfig.builder();

	/**
	 * 加载配置文件
	 */
	public void setLocation(String location) throws IOException {
		LOGGER.info("* rabbitmq-plugin loading ... location=[{}]", location);
		PropertiesWrap config = PropertiesLoadUtil.pathToProWrap(location);
		
		this.publishTemplateConfigBuilder
				.maxRetry(config.getInteger("rabbitmq.consumer-exception-retry.max", RabbitConst.DEFAULT_MAX_RETRY));

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
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	// 可靠消息发送模板 ，每次发送都会在日志表中记录，优先使用
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(name = "reliableRabbitTemplate")
	public ReliableRabbitPublishTemplate reliableRabbitTemplate(ConnectionFactory connectionFactory) {
		ReliableRabbitPublishTemplate template = new ReliableRabbitPublishTemplate(connectionFactory);
		template.setTemplateConfig(this.publishTemplateConfigBuilder.build());
		return template;
	}

	// 可靠消息消费模板
	@Bean(name = "reliableRabbitConsumeTemplate")
	public ReliableRabbitConsumeTemplate reliableRabbitConsumeTemplate() {
		return new ReliableRabbitConsumeTemplate();
	}

	// 定时队列支持
	@Bean
	public FixedTimeQueueHelper fixedTimeQueueHelper(RabbitAdmin rabbitAdmin) {
		return new FixedTimeQueueHelper(rabbitAdmin);
	}

	// ## database configuration
	@Bean(name = "pluginJdbcTemplate")
	public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
