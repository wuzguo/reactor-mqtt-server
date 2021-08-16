package com.study.iot.mqtt.protocol;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import io.netty.channel.ChannelHandler;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

public interface Protocol {

    /**
     * 支持的协议
     *
     * @param protocolType {@link ProtocolType}
     * @return {@link Boolean}
     */
    Boolean support(ProtocolType protocolType);

    /**
     * 传输
     *
     * @return {@link ProtocolTransport}
     */
    ProtocolTransport getTransport();

    /**
     * 协议定义的Handler
     *
     * @return {@link List<ChannelHandler>}
     */
    List<ChannelHandler> getHandlers();
}
