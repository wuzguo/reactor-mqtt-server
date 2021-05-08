package com.study.iot.mqtt.common.connection;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.*;
import lombok.experimental.UtilityClass;

import java.util.List;


@UtilityClass
public class MessageBuilder {

    public static MqttPublishMessage buildPub(boolean isDup, MqttQoS qoS, boolean isRetain, int messageId, String topic, ByteBuf message) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, isDup, qoS, isRetain, 0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic, messageId);
        return new MqttPublishMessage(mqttFixedHeader, mqttPublishVariableHeader, message);
    }


    public static MqttPubAckMessage buildPubAck(boolean isDup, MqttQoS qoS, boolean isRetain, int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, isDup, qoS, isRetain, 2);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(mqttFixedHeader, mqttMessageIdVariableHeader);
    }

    public static MqttConnAckMessage buildConnAck(MqttConnectReturnCode mqttConnectReturnCode, boolean sessionPresent) {
        MqttConnAckMessage mqttConnAckMessage = MqttMessageBuilders.connAck().returnCode(mqttConnectReturnCode)
                .sessionPresent(sessionPresent).build();
        return mqttConnAckMessage;
    }

    public static MqttPubAckMessage buildPubRec(int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(mqttFixedHeader, from);
    }

    public static MqttPubAckMessage buildPubRel(int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(mqttFixedHeader, from);
    }


    public static MqttPubAckMessage buildPubComp(int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(mqttFixedHeader, from);
    }

    public static MqttSubAckMessage buildFailureSubAck(int messageId, List<Integer> qos) {
        return buildSubAck(messageId, qos, MqttQoS.FAILURE);
    }

    public static MqttSubAckMessage buildSubAck(int messageId, List<Integer> qos, MqttQoS mqttQoS) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, mqttQoS, false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload payload = new MqttSubAckPayload(qos);
        return new MqttSubAckMessage(mqttFixedHeader, variableHeader, payload);
    }

    public static MqttSubAckMessage buildSubAck(int messageId, List<Integer> qos) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload payload = new MqttSubAckPayload(qos);
        return new MqttSubAckMessage(mqttFixedHeader, variableHeader, payload);
    }


    public static MqttUnsubAckMessage buildUnsubAck(int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttUnsubAckMessage(mqttFixedHeader, variableHeader);
    }

    public static MqttConnAckMessage buildConnectAck(MqttConnectReturnCode connectReturnCode) {
        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, true);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
    }

    public static MqttSubscribeMessage buildSub(int messageId, List<MqttTopicSubscription> topicSubscriptions) {
        MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(topicSubscriptions);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttSubscribeMessage(mqttFixedHeader, mqttMessageIdVariableHeader, mqttSubscribePayload);
    }

    public static MqttUnsubscribeMessage buildUnSub(int messageId, List<String> topics) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        return new MqttUnsubscribeMessage(mqttFixedHeader, variableHeader, new MqttUnsubscribePayload(topics));
    }

    public static MqttConnectMessage buildConnect(String clientId, String willTopic, String willMessage, String username, String password, boolean isUsername, boolean isPassword, boolean isWill, int willQos, int heart) {
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(MqttVersion.MQTT_3_1_1.protocolName(), MqttVersion.MQTT_3_1_1.protocolLevel(), isUsername, isPassword, false, willQos, isWill, false, heart);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientId, willTopic, isWill ? willMessage.getBytes() : null, username, isPassword ? password.getBytes() : null);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 10);
        return new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
    }
}
