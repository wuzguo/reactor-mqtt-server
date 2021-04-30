package com.study.iot.mqtt.protocal;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import io.netty.channel.ChannelHandler;

import java.util.List;

public interface Protocol {


    boolean support(ProtocolType protocolType);

    ProtocolTransport getTransport();


    List<ChannelHandler> getHandlers();

}
