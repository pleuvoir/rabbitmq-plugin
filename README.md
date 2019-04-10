
## :rocket: 可靠的RabbitMQ

[![HitCount](http://hits.dwyl.io/pleuvoir/rabbitmq-plugin.svg)](http://hits.dwyl.io/pleuvoir/rabbitmq-plugin) 
[![GitHub issues](https://img.shields.io/github/issues/pleuvoir/rabbitmq-plugin.svg)](https://github.com/pleuvoir/rabbitmq-plugin/issues)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license)](https://github.com/pleuvoir/rabbitmq-plugin/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.pleuvoir/rabbitmq-plugin.svg?label=maven%20central)](https://oss.sonatype.org/#nexus-search;quick~rabbitmq-plugin)
[![Download](https://img.shields.io/badge/downloads-master-green.svg)](https://codeload.github.com/pleuvoir/rabbitmq-plugin/zip/master)


## 介绍

本项目的目标是解决消息重复消费的问题以及方便的使用手动应答。

## 特性

- Spring支持
- 简单易用
- 自动配置
- 方便的模板
- 消费侧异常补偿重试机制
- 定时队列

## 快速开始

由于项目强依赖于`Spring`容器，因而只能在`Spring`环境下使用。

### 1.引入依赖

```xml
<dependency>
	<groupId>io.github.pleuvoir</groupId>
	<artifactId>rabbitmq-plugin</artifactId>
	<version>${latest.version}</version>
</dependency>
```

### 2. 配置文件

接着我们需要准备一份配置文件，它看起来是这样的，文件的名称我们先假定为 `rabbitmq.properties`

```java
# 消息消费异常时最大重试次数，不设置默认为3次，0代表不重试
rabbitmq.consumer-exception-retry.max=0
```

### 3. 使用Spring进行管理


如果是使用注解的项目，建议使用自动配置。就像这样：

```java
@EnableRabbitPlugin
@Configuration
public class PluginConfiguration {

}
```

只需在配置类中声明 `@EnableRabbitMQPlugin` 即可，当然这是使用默认配置。 `EnableRabbitMQPlugin` 注解包含属性  `location` 表示需要加载的配置文件位置, `location` 可以不声明，默认为 classpath 下的 `rabbitmq.properties` 文件。 


### 4. 发送消息模板

```java
@Autowired
private RabbitTemplate rabbitTemplate; // 实际上使用的是项目中定义的增强模板，会在每次发送消息时带上messageId
```

### 5. 消费消息模板

```java
@Autowired
private ReliableRabbitConsumeTemplate rabbitConsumeTemplate; // 可靠消息消费模板
```

## 示例

请参考`springboot-example`项目，提供了完整的发送消费演示。

## QA

### 解决的问题以及问题出现的原因

常规开发中，RabbitMQ有两种应答模式，自动应答和手动应答。然而两种模式都存在一些弊端：故在对消息的可靠性保障上我们一般选用手动应答。

- 自动应答

  消费者处理时出现异常，消息已从MQ Broker移除，只能手动补发消息。

- 手动应答

  存在应答时（网络抖动、超时、忘记应答、异常导致无法应答）等问题。由于未对消息进行有效应答，当消费者断开连接后，MQ Broker会将消息重新投递。关口就在于第一次收到消息的消费者业务是否处理成功？如果业务本身是幂等的，那重复消费自然没有问题。如果非幂等，那这次消息应当如何处理？

解决之道：

通过维护一张消息表，记录消息的消费情况。消息记录的提交与业务操作始终保证在同一数据库事务中。1.当业务处理成功，事务提交，但ACK时超时等异常情况。消费者可能会收到来自Broker的重复消息，由于消息在记录表中为消费成功，所以会忽略此次消息。2.当业务异常提供了自动重试机制，直到最大重试次数上限，消息状态改为消费失败。


### 依赖的外部配置

项目本身针对可靠消息的保障，尽量不侵扰`RabbitMQ`和`Spring`的集成和使用。因而`RabbitMQ`本身配置应当由应用端本身提供，数据源和数据库事务亦是如此。

### Profiles

如果项目使用  `Profiles` 来管理 spring 的环境，如  `Environment().setActiveProfiles("dev")` ，自动配置支持使用 `[profile]` 替换环境修饰符。即如果您使用了 `@EnableRabbitMQPlugin(location = "config/[profile]/rabbitmq-[profile].properties")` 进行配置，插件会寻找   `config/dev/rabbitmq-dev.properties` 文件，确保文件存在即可。使用 xml 注册的方式，不受此特性的影响，请配置实际的文件名称。

## TODO LIST

- [ ] 消费成功的消息定时删除
- [ ] 未到达Broker的消息定时重投
- [ ] Mysql支持


## 开源协议
[Apache License](LICENSE)

