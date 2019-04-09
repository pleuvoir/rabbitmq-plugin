package io.github.pleuvoir.rabbit;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;

import io.github.pleuvoir.base.kit.PropertiesLoadUtil;
import io.github.pleuvoir.base.kit.PropertiesWrap;
import io.github.pleuvoir.rabbit.reliable.ReliableMessageService;
import io.github.pleuvoir.rabbit.reliable.jdbc.JDBCReliableMessageService;
import io.github.pleuvoir.rabbit.reliable.template.PublishTemplateConfig;
import io.github.pleuvoir.rabbit.reliable.template.ReliableRabbitConsumeTemplate;
import io.github.pleuvoir.rabbit.reliable.template.ReliableRabbitPublishTemplate;
import io.github.pleuvoir.rabbit.support.creator.FixedTimeQueueHelper;

@EnableRabbit
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({"io.github.pleuvoir.rabbit.reliable"})
public class RabbitMQPluginConfiguration {

	protected static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQPluginConfiguration.class);

	public RabbitMQPluginConfiguration() {
		LOGGER.info("*rabbitmq-plugin RabbitMQPluginConfiguration loading ...");
	}

	private String rabbitmqHost;
	private Integer rabbitmqPort;
	private String rabbitmqVirtualHost;
	private String rabbitmqUsername;
	private String rabbitmqPassword;

	private String jdbcDriverClass;
	private String jdbcURL;
	private String jdbcUser;
	private String jdbcPassword;
	private int initialSize;
	private int maxActive;
	private int minIdle;
	private long maxWait;
	private long timeBetweenEvictionRunsMillis;
	private long minEvictableIdleTimeMillis;
	private String validationQuery;

	private final PublishTemplateConfig publishTemplateConfig = new PublishTemplateConfig();
	
	/**
	 * 加载配置文件
	 */
	public void setLocation(String location) throws IOException {
		PropertiesWrap config = PropertiesLoadUtil.pathToProWrap(location);
		
		rabbitmqHost = config.getString("rabbitmq.host");
		rabbitmqPort = config.getInteger("rabbitmq.port", 5672);
		rabbitmqVirtualHost = config.getString("rabbitmq.virtualHost", "/");
		rabbitmqUsername = config.getString("rabbitmq.username");
		rabbitmqPassword = config.getString("rabbitmq.password");

		jdbcDriverClass = config.getProperty("datasource.driver");
		jdbcURL = config.getProperty("datasource.url");
		jdbcUser = config.getProperty("datasource.username");
		jdbcPassword = config.getProperty("datasource.password");
		
		initialSize = config.getInteger("datasource.initial-size", 2);
		maxActive = config.getInteger("datasource.max-active", 5);
		maxWait = config.getInteger("datasource.max-wait", 5);
		minIdle = config.getInteger("datasource.min-idle", 2);
		timeBetweenEvictionRunsMillis = config.getLong("datasource.time-between-eviction-runs-millis", 60_000L);
		minEvictableIdleTimeMillis = config.getLong("datasource.min-evictable-idle-time-millis", 300_000L);
		validationQuery = config.getProperty("datasource.validation-query");
		
		Assert.notNull(rabbitmqHost, "rabbitmqHost must be non-null.");
		Assert.notNull(jdbcDriverClass, "jdbcDriverClass must be non-null.");
		Assert.notNull(jdbcURL, "jdbcURL must be non-null.");
		Assert.notNull(jdbcUser, "jdbcUser must be non-null.");
		Assert.notNull(jdbcPassword, "jdbcPassword must be non-null.");
		Assert.notNull(validationQuery, "validationQuery must be non-null.");
		
		publishTemplateConfig
				.setMaxRetry(config.getInteger("rabbitmq.consumer.max-retry", RabbitConst.DEFAULT_MAX_RETRY));
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
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	// 可靠消息发送模板 ，每次发送都会在日志表中记录，优先使用
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(name = "reliableRabbitTemplate")
	public ReliableRabbitPublishTemplate reliableRabbitTemplate(ConnectionFactory connectionFactory) {
		
		ReliableRabbitPublishTemplate template = new ReliableRabbitPublishTemplate(connectionFactory);
		template.setTemplateConfig(publishTemplateConfig);
		return template;
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


	// ## database configuration

	@Bean(name = "pluginDataSource")
	public DataSource pluginDataSource() {
		LOGGER.info("*rabbitmq-plugin 使用数据源配置 [jdbcURL={},jdbcUser={}]", this.jdbcURL, this.jdbcUser);
		
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName(this.jdbcDriverClass);
		ds.setUrl(this.jdbcURL);
		ds.setUsername(this.jdbcUser);
		ds.setPassword(this.jdbcPassword);
		ds.setInitialSize(this.initialSize);
		ds.setMaxActive(this.maxActive);
		ds.setMinIdle(this.minIdle);
		ds.setMaxWait(this.maxWait);
		ds.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
		ds.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
		ds.setValidationQuery(this.validationQuery);
		ds.setTestOnBorrow(false);
		ds.setTestOnReturn(false);
		ds.setTestWhileIdle(true);
		return ds;
	}

	@Bean(name = "pluginJdbcTemplate")
	public JdbcTemplate getJdbcTemplate(@Qualifier("pluginDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
