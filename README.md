# MQTT 学习项目

### 简介

这是个人学习MQTT协议的项目，主要记录学习过程中所写的代码，仅供参考。

### 模块及说明

- reactor-mqtt-akka： 使用AKKA进行集群内实例间通讯和集群实例管理。
- reactor-mqtt-api：服务提供的RESTful的API接口。
- reactor-mqtt-auth：认证鉴权模块，提供认证鉴权功能。
- reactor-mqtt-broker：Broker模块，服务端主程序。
- reactor-mqtt-client：客户端模块，提供连接服务的功能。
- reactor-mqtt-store： 缓存或持久化模块，提供Redis，Hbase，Ignite存储。
- reactor-mqtt-common： 基础模块。
- reactor-mqtt-protocol： 协议模块。
- reactor-mqtt-transport：消息处理模块。
- reactor-mqtt-session：MQTT session管理模块。

### 依赖工具

要运行本项目首先要保证安装以下工具，基础工具安装步骤请自行百度。

- JDK 1.8+
- Maven 3+
- Netty 4.1+
- Ignite 2.6+
- Hbase 2.0 +
- redis 

### 功能简介

本项目主要是为了熟悉物联网领域常用的协议而建的项目，计划实现行业内常用的协议，如MQTT、Websocket、coAP、XMPP、Http等。

MQTT 协议文档：http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html

MQTT协议中文文档：http://mqtt.p2hp.com/mqtt311



本项目采用集群部署，支持集群内Seesion共享，采用AKKA进行集群内实例间通讯和集群实例管理。

akka官方文档：https://akka.io/docs/

akka中文文档：https://github.com/guobinhit/akka-guide



本项目采用HBase存储消息，Redis存储连接信息。



### 架构图

**暂缺**



### 使用说明

#### 启动Broker服务

​	直接运行 reactor-mqtt-broker 模块中的 BrokerApplication 主程序即可启动。当控制台出现以下信息时表示启动成功：

```shell
o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8800
c.s.iot.mqtt.server.BrokerApplication    : Started BrokerApplication in 8.496 seconds (JVM running for 9.526)
c.s.i.mqtt.protocol.mqtt.MqttTransport   : mqtt protocol host: localhost port: 1800
```

#### 连接服务端

1. 使用自带的Client连接

直接在 reactor-mqtt-client 模块中运行 TestConsumer类即可。当控制台打印以下日志时说明连接成功：

```sh
c.s.i.mqtt.protocol.mqtt.MqttTransport   : connected successes
```

2. 使用客户端工具连接

配置参数如图所示，配置完成后点击连接按钮。

![](./images/2.png)

连接成功后如图所示：

![](./images/3.png)

#### 发布订阅消息

连接成功后可以通过客户端工具的Topic正常发布消息。也可以使用  reactor-mqtt-client 模块中运行 TestProducer类发布消息。



### 已实现功能

1. MQTT Broker 的基本逻辑。
2. MQTT Broker 实例间通Topic发布订阅消息。
3. MQTT client 的连接逻辑。
4. HBase 存储的功能封装。
5. Redis 工具类。
6. Akka进群管理功能及实例间通讯工具。
7. 常用的工具类。
8. ignite的基础配置。
9. 认证模块。
10. metric基础模块。

### 待实现功能

1. MQTT Broker 功能完成，集群内Session共享。
2. MQTT 消息队列支持，保证消息的有序性。
3. 连接对象的存储。
4. AKKA集群内实例掉线后的处理。
5. metric功能完善。
6. coAP、XMPP、Websocket协议支持。
7. 代码优化，重构。
8. 测试。

### 说明

本项目没有经过严格的功能测试，没有压力测试，没有上过生产，请谨慎使用。



### 持续更新...

根据学习进度，持续更新....

### 纠错

欢迎大家指出不足，如有任何疑问，请邮件联系 wuzguo@gmail.com 或者直接修复并提交 Pull Request。

### 参考项目