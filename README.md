
## :rocket: 可靠的RabbitMQ

[![HitCount](http://hits.dwyl.io/pleuvoir/rabbitmq-plugin.svg)](http://hits.dwyl.io/pleuvoir/rabbitmq-plugin) 
[![GitHub issues](https://img.shields.io/github/issues/pleuvoir/rabbitmq-plugin.svg)](https://github.com/pleuvoir/rabbitmq-plugin/issues)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license)](https://github.com/pleuvoir/rabbitmq-plugin/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.pleuvoir/rabbitmq-plugin.svg?label=maven%20central)](https://oss.sonatype.org/#nexus-search;quick~rabbitmq-plugin)
[![Download](https://img.shields.io/badge/downloads-master-green.svg)](https://codeload.github.com/pleuvoir/rabbitmq-plugin/zip/master)


## 介绍

本项目的目标是解决消息幂等的问题以及方便的使用手动应答，在手动确认模式下尽力处理服务异常时可恢复性的故障。

## 特性

- Spring支持
- 简单易用
- 方便的模板
- 消费侧异常重试机制
- 定时消息

## 快速开始

由于项目强依赖于`Spring`容器，因而只能在`Spring`环境下使用。

### 引入依赖

```xml
<dependency>
  <groupId>io.github.pleuvoir</groupId>
  <artifactId>rabbitmq-plugin</artifactId>
  <version>${latest.version}</version>
</dependency>
```

### 使用Spring进行管理

```java
@EnableRabbitPlugin(maxRetry = 1)
@Configuration
public class PluginConfiguration {

}
```

`maxRetry`代表当业务异常时进行重试的次数。

### 发送消息模板

框架中定义了两种消息发送模版：

可靠消息发送模板，会在每次发送消息时带上messageId，并且每次发送都会在数据库中记录消息日志：

```java
@Autowired
private RabbitTemplate rabbitTemplate;
```

如果想使用普通模版：

```java
@Resource(name = "rabbitTemplate")
private RabbitTemplate rabbitConsumeTemplate; 
```

区别是什么？

可靠消息发送模板发送的消息只能由可靠消息消费模板来处理，普通模版则无此限制。

### 可靠消息消费模板

继承`AbstractRetryRabbitConsumeTemplate`即可，注意：使用此消费模版的消息，必须是来自可靠消息发送模板的消息，否则会被忽略。

```java
@Service
public class NormalConsumer extends AbstractRetryRabbitConsumeTemplate {

    @Override
    protected void handler(String data) {
        LOGGER.info("NormalConsumer 接受到新消息：{}", data);
    }

    @Override
    protected void exceptionHandler(Message message, Throwable e) {
    	LOGGER.error("exception occur",e);
    }
}
```

### 定时消息

提供了到达指定时间投递消息的功能，有别于`FIFO`延迟队列实现的延迟消息。

## 示例

请参考`springboot-example`项目，提供了完整的发送消费演示。

## QA

### 解决的问题以及问题出现的原因

常规开发中，RabbitMQ有两种应答模式，自动应答和手动应答。然而两种模式都存在一些弊端：

- 自动应答

  消费者处理时出现异常，消息已从MQ Broker移除，只能手动补发消息。

- 手动应答

  存在应答时（网络抖动、超时、忘记应答、异常导致无法应答）等问题。由于未对消息进行有效应答，当消费者断开连接后，MQ Broker会将消息重新投递。关口是第一次收到消息的消费者业务是否处理成功？如果业务本身是幂等的，那重复消费自然没有问题。如果非幂等，那这次消息应当如何处理？

解决之道：

通过维护一张消息表，记录消息的消费情况。消息记录的提交与业务操作始终保证在同一数据库事务中。1.当业务处理成功，事务提交，但ACK时超时等异常情况。消费者可能会收到来自Broker的重复消息，由于消息在记录表中为消费成功，所以会忽略此次消息。2.当业务异常提供了自动重试机制，直到最大重试次数上限，消息状态改为消费失败。


### 依赖的外部配置

项目本身针对可靠消息的保障，尽量不侵扰`RabbitMQ`和`Spring`的集成和使用。因而`RabbitMQ`的配置应由应用端本身提供，数据源和数据库事务亦是如此。

### 消息日志记录

当项目启动时会根据数据库类型自动创建消息日志表。

### 重试机制

当业务异常时，可靠消息处理框架会进行多次重试，重试次数可通过`@EnableRabbitPlugin(maxRetry = 1)`属性指定。当重试次数到达设定的最高值时，消息状态会变为失败。**注意：重试机制只会在手动确认模式下生效。**

## TODO LIST

- [ ] 消费成功的消息定时删除
- [ ] 未到达Broker的消息定时重投
- [ ] Mysql支持


## 开源协议
[Apache License](LICENSE)

