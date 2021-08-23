package com.study.iot.mqtt.protocol;


import com.study.iot.mqtt.protocol.session.ClientSession;
import io.netty.util.AttributeKey;
import lombok.experimental.UtilityClass;
import reactor.core.Disposable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

@UtilityClass
public class AttributeKeys {

    /**
     * 客户端连接
     */
    public AttributeKey<ClientSession> clientConnection = AttributeKey.valueOf("client_connection");

    /**
     * 关闭连接
     */
    public AttributeKey<Disposable> closeConnection = AttributeKey.valueOf("close_connection");

    /**
     * 连接标识
     */
    public AttributeKey<String> identity = AttributeKey.valueOf("identity");

    /**
     * 连接存活时间
     */
    public AttributeKey<Integer> keepalive = AttributeKey.valueOf("keep_alive");
}
