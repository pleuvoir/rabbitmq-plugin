package io.github.pleuvoir.rabbit.autoconfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import io.github.pleuvoir.rabbit.RabbitConst;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableRabbitPluginRegistrar.class)
public @interface EnableRabbitPlugin {

	/**
	 * this is plugin name, it must be no-empty value.
	 */
	String name() default "rabbitmq-plugin";

	/**
	 * 最大重试次数
	 */
	int maxRetry() default RabbitConst.DEFAULT_MAX_RETRY;

}
