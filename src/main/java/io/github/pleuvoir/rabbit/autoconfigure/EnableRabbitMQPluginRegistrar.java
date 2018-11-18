package io.github.pleuvoir.rabbit.autoconfigure;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;

import io.github.pleuvoir.base.autoconfigure.AbstractPluginRegistrar;
import io.github.pleuvoir.rabbit.RabbitMQConfiguration;

public class EnableRabbitMQPluginRegistrar extends AbstractPluginRegistrar {

	@Override
	protected Class<? extends Annotation> getEnableAnnotationClass() {
		return EnableRabbitMQPlugin.class;
	}

	@Override
	protected Class<?> defaultConfigurationClass() {
		return RabbitMQConfiguration.class;
	}

	@Override
	protected void customize(BeanDefinitionRegistry registry, AnnotationAttributes attributes,
			BeanDefinitionBuilder definition, BeanFactory beanFactory) {
		definition.addPropertyValue("location", locationFormat(attributes.getString("location")));
	}

}
