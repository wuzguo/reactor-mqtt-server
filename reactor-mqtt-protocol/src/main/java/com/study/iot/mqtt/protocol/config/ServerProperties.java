package com.study.iot.mqtt.protocol.config;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 14:58
 */

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ServerProperties extends ConnectProperties {

    /**
     * 发送缓冲区大小 默认 32k
     */
    private Integer sendBufSize;

    /**
     * 接收缓冲区大小 默认 32k
     */
    private Integer revBufSize;

    /**
     * Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值 这里默认设置 128
     */
    private Integer backlog;

    /**
     * Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制， 需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
     */
    private Boolean keepAlive;

    /**
     * Socket参数，立即发送数据，默认值为True（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用， 该算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
     * 如果需要发送一些较小的报文，则需要禁用该算法。 Netty默认禁用该算法，从而最小化报文传输延时
     */
    private Boolean noDelay;

    /**
     * 缓存策略
     */
    private CacheStrategy strategy;

    /**
     * 协议
     */
    private Set<ProtocolType> protocols;
}
