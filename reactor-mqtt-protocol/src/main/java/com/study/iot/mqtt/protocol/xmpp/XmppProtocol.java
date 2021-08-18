package com.study.iot.mqtt.protocol.xmpp;

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
 * @date 2021/8/18 10:43
 */

public class XmppProtocol implements Protocol {

    @Override
    public Boolean support(ProtocolType protocolType) {
        return protocolType == ProtocolType.XMPP;
    }

    @Override
    public ProtocolTransport getTransport() {
        return new XmppTransport(this);
    }

    @Override
    public List<ChannelHandler> getHandlers() {
        return null;
    }
}
