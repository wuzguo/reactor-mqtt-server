package com.study.iot.mqtt.store.mapper;

import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.store.hbase.RowMapper;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 10:51
 */

public class SessionMessageRowMapper implements RowMapper<SessionMessage> {

    /**
     * 列族
     */
    private final static byte[] COLUMN_FAMILY = "message".getBytes();

    /**
     * ROW
     */
    private final static byte[] ROW = "row".getBytes();

    /**
     * 客户端标识
     */
    private final static byte[] IDENTITY = "identity".getBytes();

    /**
     * SessionId
     */
    private final static byte[] SESSION_ID = "sessionId".getBytes();

    /**
     * 消息ID
     */
    private final static byte[] MESSAGE_ID = "messageId".getBytes();

    /**
     * TOPIC
     */
    private final static byte[] TOPIC = "topic".getBytes();

    /**
     * 保持
     */
    private final static byte[] RETAIN = "retain".getBytes();

    /**
     * 消息类型
     */
    private final static byte[] MESSAGE_TYPE = "messageType".getBytes();

    /**
     * 消息质量
     */
    private final static byte[] QOS = "qos".getBytes();

    /**
     * 是否重发
     */
    private final static byte[] DUP = "dup".getBytes();

    /**
     * 消息
     */
    private final static byte[] COPY_BYTE_BUF = "copyByteBuf".getBytes();


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
}
