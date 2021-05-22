package com.study.iot.mqtt.store.mapper;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.store.hbase.TableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 10:51
 */

@Slf4j
public class SessionMessageMapper implements TableMapper<SessionMessage> {

    /**
     * 列族
     */
    private final static byte[] COLUMN_FAMILY = SessionMessage.COLUMN_FAMILY.getBytes(StandardCharsets.UTF_8);

    /**
     * ROW
     */
    private final static byte[] ROW = "row".getBytes(StandardCharsets.UTF_8);

    /**
     * 客户端标识
     */
    private final static byte[] IDENTITY = "identity".getBytes(StandardCharsets.UTF_8);

    /**
     * SessionId
     */
    private final static byte[] SESSION_ID = "sessionId".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息ID
     */
    private final static byte[] MESSAGE_ID = "messageId".getBytes(StandardCharsets.UTF_8);

    /**
     * TOPIC
     */
    private final static byte[] TOPIC = "topic".getBytes(StandardCharsets.UTF_8);

    /**
     * 保持
     */
    private final static byte[] RETAIN = "retain".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息类型
     */
    private final static byte[] MESSAGE_TYPE = "messageType".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息质量
     */
    private final static byte[] QOS = "qos".getBytes(StandardCharsets.UTF_8);

    /**
     * 是否重发
     */
    private final static byte[] DUP = "dup".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息
     */
    private final static byte[] COPY_BYTE_BUF = "copyByteBuf".getBytes(StandardCharsets.UTF_8);


    @Override
    public SessionMessage mapRow(Result result, int rowNum) throws Exception {
        return SessionMessage.builder()
                .row(Bytes.toString(result.getValue(COLUMN_FAMILY, ROW)))
                .identity(Bytes.toString(result.getValue(COLUMN_FAMILY, IDENTITY)))
                .sessionId(Bytes.toString(result.getValue(COLUMN_FAMILY, SESSION_ID)))
                .messageId(Integer.valueOf(Bytes.toString(result.getValue(COLUMN_FAMILY, MESSAGE_ID))))
                .topic(Bytes.toString(result.getValue(COLUMN_FAMILY, TOPIC)))
                .retain(Boolean.getBoolean(Bytes.toString(result.getValue(COLUMN_FAMILY, RETAIN))))
                .messageType(Integer.valueOf(Bytes.toString(result.getValue(COLUMN_FAMILY, MESSAGE_TYPE))))
                .qos(Integer.valueOf(Bytes.toString(result.getValue(COLUMN_FAMILY, QOS))))
                .dup(Boolean.getBoolean(Bytes.toString(result.getValue(COLUMN_FAMILY, DUP))))
                .copyByteBuf(result.getValue(COLUMN_FAMILY, COPY_BYTE_BUF))
                .build();
    }

    @Override
    public List<Mutation> mutations(SessionMessage message) throws Exception {
        // rowKey
        Put put = new Put(Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), ROW, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), IDENTITY, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), SESSION_ID, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), MESSAGE_ID, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), TOPIC, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), RETAIN, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), MESSAGE_TYPE, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), QOS, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), DUP, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), COPY_BYTE_BUF, Bytes.toBytes(message.getRow()));
        return Lists.newArrayList(put);
    }
}
