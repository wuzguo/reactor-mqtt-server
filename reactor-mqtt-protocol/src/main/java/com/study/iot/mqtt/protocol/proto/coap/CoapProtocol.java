package com.study.iot.mqtt.protocol.proto.coap;

import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.Protocol;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import io.netty.channel.ChannelHandler;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 10:42
 */

public class CoapProtocol implements Protocol {

    @Override
    public Boolean support(ProtocolType protocolType) {
        return protocolType == ProtocolType.COAP;
    }

    @Override
    public ProtocolTransport getTransport() {
        return new CoapTransport(this);
    }

    @Override
    public List<ChannelHandler> getHandlers() {
        return null;
    }
}
