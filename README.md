
## :rocket: 方便的使用 rabbitmq

[![HitCount](http://hits.dwyl.io/pleuvoir/rabbitmq-plugin.svg)](http://hits.dwyl.io/pleuvoir/rabbitmq-plugin) 
[![GitHub issues](https://img.shields.io/github/issues/pleuvoir/rabbitmq-plugin.svg)](https://github.com/pleuvoir/rabbitmq-plugin/issues)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license)](https://github.com/pleuvoir/rabbitmq-plugin/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.pleuvoir/rabbitmq-plugin.svg?label=maven%20central)](https://oss.sonatype.org/#nexus-search;quick~rabbitmq-plugin)
[![Download](https://img.shields.io/badge/downloads-master-green.svg)](https://codeload.github.com/pleuvoir/rabbitmq-plugin/zip/master)

### 特性

- 简单易用
- 自动配置
- 方便的 API

### 快速开始

#### 1.引入依赖

```xml
<dependency>
	<groupId>io.github.pleuvoir</groupId>
	<artifactId>rabbitmq-plugin</artifactId>
	<version>${latest.version}</version>
</dependency>
```

#### 2. 配置文件

接着我们需要准备一份配置文件，它看起来是这样的，文件的名称我们先假定为 `rabbitmq.properties`

```xml
rabbitmq.host=127.0.0.1
rabbitmq.port=5672
rabbitmq.virtualHost=/
rabbitmq.username=guest
rabbitmq.password=guest
```

#### 3. 使用 spring 进行管理

对于使用 `xml` 进行配置的项目，只需要如下声明即可。

```xml
<bean class="io.github.pleuvoir.rabbit.RabbitMQConfiguration">
	<property name="location" value="rabbitmq.properties" />
</bean>
```

如果是使用注解的项目，建议使用自动配置。就像这样：

```java
@EnableRabbitMQPlugin
@Configuration
public class PluginConfiguration {

}
```

只需在配置类中声明 `@EnableRabbitMQPlugin` 即可，当然这是使用默认配置。 `EnableRabbitMQPlugin` 注解有一个属性是  `location` 表示需要加载的配置文件位置, `location` 可以不声明，默认为  classpath 下的 `rabbitmq.properties` 文件。 

如果项目使用  `Profiles` 来管理 spring 的环境，如  `Environment().setActiveProfiles("dev")` ，自动配置支持使用 `[profile]` 替换环境修饰符。即如果您使用了 `@EnableRabbitMQPlugin(location = "config/[profile]/rabbitmq-[profile].properties")` 进行配置，插件会寻找   `config/dev/rabbitmq-dev.properties` 文件，确保文件存在即可。使用 xml 注册的方式，不受此特性的影响，请配置实际的文件名称。


### TODO LIST

- [ ] STOMP 支持
- [ ] More API

### 开源协议
[Apache License](LICENSE)

