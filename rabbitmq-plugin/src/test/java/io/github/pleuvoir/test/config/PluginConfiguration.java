package io.github.pleuvoir.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.github.pleuvoir.rabbit.autoconfigure.EnableRabbitPlugin;

@EnableRabbitPlugin
@Configuration
@ComponentScan("io.github.pleuvoir.test")
public class PluginConfiguration {

}
