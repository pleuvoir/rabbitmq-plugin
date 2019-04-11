package io.github.pleuvoir.rabbit.autoconfigure;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;

import io.github.pleuvoir.rabbit.RabbitPluginConfiguration;
import io.github.pleuvoir.rabbit.reliable.template.PublishTemplateConfig;

public class EnableRabbitPluginRegistrar extends AbstractPluginRegistrar {

	@Override
	protected Class<? extends Annotation> getEnableAnnotationClass() {
		return EnableRabbitPlugin.class;
	}

	@Override
	protected Class<?> defaultConfigurationClass() {
		return RabbitPluginConfiguration.class;
	}

	@Override
	protected void customize(BeanDefinitionRegistry registry, AnnotationAttributes attributes,
			BeanDefinitionBuilder definition, BeanFactory beanFactory) {

		definition.addPropertyValue("publishTemplateConfigBuilder",
				PublishTemplateConfig.builder().
				maxRetry(attributes.getNumber("maxRetry").intValue()));
	}

}
