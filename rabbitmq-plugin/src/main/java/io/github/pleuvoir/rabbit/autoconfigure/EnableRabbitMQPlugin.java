package io.github.pleuvoir.rabbit.autoconfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableRabbitMQPluginRegistrar.class)
public @interface EnableRabbitMQPlugin {

	/**
	 * this is plugin name, it must be no-empty value.
	 */
	String name() default "rabbitmq-plugin";

	/**
	 * the location of resource file.
	 */
	String location() default "rabbitmq.properties";

}
