package io.github.pleuvoir.springboot.example;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;

import io.github.pleuvoir.rabbit.autoconfigure.EnableRabbitMQPlugin;

@Configuration
@EnableTransactionManagement
@MapperScan("io.github.pleuvoir.springboot.example.dao")


@EnableRabbitMQPlugin(location = "config/rabbitmq-[profile].properties")  // 看这里，启用
public class SpringbootExampleConfiguration {

	
	/*
	 * 
	 * 以下是 rabbitmq 需要进行设置的地方<br>
	 * 
	 * 其中rabbitListenerContainerFactory是自动监听工厂
	 * manualRabbitListenerContainerFactory是手动监听工厂
	 * 
	 * 提示：在spring中如果需要同时使用手动确认和自动确认模式，是需要使用两个监听工厂的。这里我们使用手动确认模式来测试我们的可靠消息处理框架
	 */
	
	/**
	 *rabbit
	 */
	@Bean(name = "rabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMaxConcurrentConsumers(20);
		factory.setAcknowledgeMode(AcknowledgeMode.NONE);
		return factory;
	}
	
	@Bean(name = "manualRabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory manualRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMaxConcurrentConsumers(1);
		factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		factory.setPrefetchCount(1);
		return factory;
	}

	
	/*
	 * 
	 * 以下都是和框架无关的
	 * 
	 */
	
	/**
	 * mybatis-plus
	 */
	@Bean("sqlSessionFactory")
	public MybatisSqlSessionFactoryBean getMybatisSqlSessionFactoryBean(DataSource dataSource) throws IOException {
		MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);

		factoryBean.setConfigLocation(new ClassPathResource("mapping-config.xml"));
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath:META-INF/mapper/**/*Mapper.xml"));

		GlobalConfiguration globalConfig = new GlobalConfiguration();
		globalConfig.setIdType(IdType.UUID.getKey());
		globalConfig.setDbType(DBType.ORACLE.getDb());
		globalConfig.setDbColumnUnderline(true);
		factoryBean.setGlobalConfig(globalConfig);

		factoryBean.setPlugins(new Interceptor[]{new PaginationInterceptor(), new OptimisticLockerInterceptor()});
		return factoryBean;
	}


	@Bean("transactionManager")
	public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}