package com.study.iot.mqtt.protocol;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import io.netty.handler.codec.mqtt.MqttVersion;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

@UtilityClass
public final class MessageBuilder {

    /**
     * 构造发布消息
     *
     * @param isDup     是否重复分发标志
     * @param qoS       消息质量等级
     * @param isRetain  保留标志
     * @param messageId 消息ID
     * @param topic     主题
     * @param message   消息内容
     * @return {@link MqttPublishMessage}
     */
    public static MqttPublishMessage buildPub(boolean isDup, MqttQoS qoS, boolean isRetain, int messageId, String topic,
        byte[] message) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, isDup, qoS, isRetain, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, messageId);
        return new MqttPublishMessage(fixedHeader, variableHeader, Unpooled.wrappedBuffer(message));
    }

    /**
     * 构造MQTT消息
     *
     * @param messageType     消息类型
     * @param isDup           是否重复分发标志
     * @param qoS             消息质量等级
     * @param isRetain        保留标志
     * @param remainingLength 剩余长度
     * @return {@link MqttMessage}
     */
    public static MqttMessage buildPing(MqttMessageType messageType, boolean isDup, MqttQoS qoS,
        boolean isRetain, int remainingLength) {
        return new MqttMessage(new MqttFixedHeader(messageType, isDup, qoS, isRetain, remainingLength));
    }

    /**
     * 构造发布确认消息
     *
     * @param isDup     是否重复分发标志
     * @param qoS       消息质量等级
     * @param isRetain  保留标志
     * @param messageId 消息ID
     * @return {@link MqttPubAckMessage}
     */
    public static MqttPubAckMessage buildPubAck(boolean isDup, MqttQoS qoS, boolean isRetain, int messageId) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, isDup, qoS, isRetain, 2);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(fixedHeader, variableHeader);
    }

    /**
     * 构造确认连接请求消息
     *
     * @param mqttConnectReturnCode {@link MqttConnectReturnCode}
     * @param sessionPresent        {@link Boolean} 是否保存Session
     * @return {@link MqttConnAckMessage}
     */
    public static MqttConnAckMessage buildConnAck(MqttConnectReturnCode mqttConnectReturnCode, boolean sessionPresent) {
        return MqttMessageBuilders.connAck().returnCode(mqttConnectReturnCode).sessionPresent(sessionPresent).build();
    }

    /**
     * 构造连接确认消息
     *
     * @param connectReturnCode {@link MqttConnectReturnCode}
     * @return {@link MqttConnAckMessage}
     */
    public static MqttConnAckMessage buildConnectAck(MqttConnectReturnCode connectReturnCode) {
        MqttConnAckVariableHeader variableHeader = new MqttConnAckVariableHeader(connectReturnCode, true);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(
            MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        return new MqttConnAckMessage(fixedHeader, variableHeader);
    }

    /**
     * 构造发布确认消息
     *
     * @param messageId 消息ID
     * @return {@link MqttPubAckMessage}
     */
    public static MqttPubAckMessage buildPubRec(int messageId) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_LEAST_ONCE,
            false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(fixedHeader, variableHeader);
    }

    /**
     * 构造发布确认消息
     *
     * @param messageId 消息ID
     * @return {@link MqttPubAckMessage}
     */
    public static MqttPubAckMessage buildPubRel(int messageId) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE,
            false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(fixedHeader, variableHeader);
    }

    /**
     * 构造发布确认消息
     *
     * @param messageId 消息ID
     * @return {@link MqttPubAckMessage}
     */
    public static MqttPubAckMessage buildPubComp(int messageId) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE,
            false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(fixedHeader, variableHeader);
    }

    /**
     * 构造订阅确认消息
     *
     * @param messageId 消息ID
     * @param qos       {@link List} 消息质量等级
     * @return {@link MqttPubAckMessage}
     */
    public static MqttSubAckMessage buildFailureSubAck(int messageId, List<Integer> qos) {
        return buildSubAck(messageId, qos, MqttQoS.FAILURE);
    }

    /**
     * 构造订阅确认消息
     *
     * @param messageId 消息ID
     * @param qos       {@link List} 消息质量等级
     * @param mqttQoS   消息质量等级
     * @return {@link MqttSubAckMessage}
     */
    public static MqttSubAckMessage buildSubAck(int messageId, List<Integer> qos, MqttQoS mqttQoS) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, mqttQoS, false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload payload = new MqttSubAckPayload(qos);
        return new MqttSubAckMessage(fixedHeader, variableHeader, payload);
    }

    /**
     * 构造订阅确认消息
     *
     * @param messageId 消息ID
     * @param qos       消息质量等级
     * @return {@link MqttSubAckMessage}
     */
    public static MqttSubAckMessage buildSubAck(int messageId, List<Integer> qos) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE,
            false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload payload = new MqttSubAckPayload(qos);
        return new MqttSubAckMessage(fixedHeader, variableHeader, payload);
    }

    /**
     * 构造取消订阅确认消息
     *
     * @param messageId 消息ID
     * @return {@link MqttUnsubAckMessage}
     */
    public static MqttUnsubAckMessage buildUnsubAck(int messageId) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE,
            false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttUnsubAckMessage(fixedHeader, variableHeader);
    }

    /**
     * 构造订阅消息
     *
     * @param messageId          消息ID
     * @param topicSubscriptions {@link MqttTopicSubscription}
     * @return {@link MqttSubscribeMessage}
     */
    public static MqttSubscribeMessage buildSub(int messageId, List<MqttTopicSubscription> topicSubscriptions) {
        MqttSubscribePayload payload = new MqttSubscribePayload(topicSubscriptions);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE,
            false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttSubscribeMessage(fixedHeader, variableHeader, payload);
    }

    /**
     * 构造取消订阅消息
     *
     * @param messageId 消息ID
     * @param topics    主题集合
     * @return {@link MqttUnsubscribeMessage}
     */
    public static MqttUnsubscribeMessage buildUnSub(int messageId, List<String> topics) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE,
            false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttUnsubscribeMessage(fixedHeader, variableHeader, new MqttUnsubscribePayload(topics));
    }

    /**
     * 构造连接消息
     *
     * @param identity             连接标识符
     * @param willTopic            遗嘱主题
     * @param willMessage          遗嘱内容
     * @param username             用户名
     * @param password             密码
     * @param isUsername           是否有有用户名
     * @param isPassword           是否有密码
     * @param isWill               是否有遗嘱
     * @param willQos              遗嘱消息质量等级
     * @param keepAliveTimeSeconds 连接存活时间
     * @return {@link MqttConnectMessage}
     */
    public static MqttConnectMessage buildConnect(String identity, String willTopic, String willMessage,
        String username, String password, boolean isUsername, boolean isPassword, boolean isWill, int willQos,
        int keepAliveTimeSeconds) {
        MqttConnectVariableHeader variableHeader = new MqttConnectVariableHeader(
            MqttVersion.MQTT_3_1_1.protocolName(), MqttVersion.MQTT_3_1_1.protocolLevel(), isUsername, isPassword,
            false, willQos, isWill, false, keepAliveTimeSeconds);
        MqttConnectPayload payload = new MqttConnectPayload(identity, willTopic,
            isWill ? willMessage.getBytes() : null, username, isPassword ? password.getBytes() : null);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE,
            false, 10);
        return new MqttConnectMessage(fixedHeader, variableHeader, payload);
    }
}
