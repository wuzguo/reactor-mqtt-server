package com.study.iot.mqtt.protocal.config;


import com.study.iot.mqtt.protocal.ChannelManager;
import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.MessageHandler;
import com.study.iot.mqtt.protocal.TopicManager;
import com.study.iot.mqtt.protocal.handler.MemoryChannelManager;
import com.study.iot.mqtt.protocal.handler.MemoryMessageHandler;
import com.study.iot.mqtt.protocal.handler.MemoryTopicManager;
import lombok.Data;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Data
public class ServerConfiguration implements ConnectConfiguration {

    private String ip;

    private int port;

    private String protocol;

    private int heart = 60;

    private boolean log;

    private boolean ssl;

    /**
     * 发送缓冲区大小
     * 默认 32k
     */
    private int sendBufSize = 32 * 1024;

    /**
     * 接收缓冲区大小
     * 默认 32k
     */
    private int revBufSize = 32 * 1024;

    /**
     * Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值
     * 这里默认设置 128
     */
    private int backlog = 128;

    /**
     * Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，
     * 需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
     */
    private boolean keepAlive = false;
    /**
     * Socket参数，立即发送数据，默认值为True（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，
     * 该算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
     * 如果需要发送一些较小的报文，则需要禁用该算法。
     * Netty默认禁用该算法，从而最小化报文传输延时
     */
    private boolean noDelay = true;


    private BiFunction<String, String, Boolean> auth = (user, pass) -> true;


    private Consumer<Throwable> throwableConsumer = throwable -> {
    };

    private MessageHandler messageHandler = new MemoryMessageHandler();

    private ChannelManager channelManager= new MemoryChannelManager();

    private TopicManager topicManager = new MemoryTopicManager();

    @Override
    public void checkConfig() {
        Objects.requireNonNull(ip, "ip is not null");
        Objects.requireNonNull(protocol, "protocol is not null");
    }
}
