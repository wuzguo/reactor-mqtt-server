package com.study.iot.mqtt.store.mapper;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.message.SessionMessage;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.store.hbase.TableMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 10:51
 */

@Slf4j
public class WillMessageMapper implements TableMapper<WillMessage> {

    /**
     * 列族
     */
    private final static byte[] COLUMN_FAMILY = WillMessage.COLUMN_FAMILY.getBytes(StandardCharsets.UTF_8);

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
     * 消息质量
     */
    private final static byte[] QOS = "qos".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息
     */
    private final static byte[] COPY_BYTE_BUF = "copyByteBuf".getBytes(StandardCharsets.UTF_8);


    @Override
    public WillMessage mapRow(Result result, int rowNum) throws Exception {
        return WillMessage.builder()
            .row(Bytes.toString(result.getValue(COLUMN_FAMILY, ROW)))
            .identity(Bytes.toString(result.getValue(COLUMN_FAMILY, IDENTITY)))
            .sessionId(Bytes.toString(result.getValue(COLUMN_FAMILY, SESSION_ID)))
            .messageId(Bytes.toInt(result.getValue(COLUMN_FAMILY, MESSAGE_ID)))
            .topic(Bytes.toString(result.getValue(COLUMN_FAMILY, TOPIC)))
            .retain(Boolean.getBoolean(Bytes.toString(result.getValue(COLUMN_FAMILY, RETAIN))))
            .qos(Bytes.toInt(result.getValue(COLUMN_FAMILY, QOS)))
            .copyByteBuf(result.getValue(COLUMN_FAMILY, COPY_BYTE_BUF))
            .build();
    }

    @Override
    public List<Mutation> mutations(WillMessage message) throws Exception {
        // rowKey
        Put put = new Put(Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), ROW, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), IDENTITY, Bytes.toBytes(message.getIdentity()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), SESSION_ID, Bytes.toBytes(message.getSessionId()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), MESSAGE_ID, Bytes.toBytes(message.getMessageId()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), TOPIC, Bytes.toBytes(message.getTopic()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), RETAIN, Bytes.toBytes(message.getRetain()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), QOS, Bytes.toBytes(message.getQos()));
        put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), COPY_BYTE_BUF, message.getCopyByteBuf());
        return Lists.newArrayList(put);
    }
}
