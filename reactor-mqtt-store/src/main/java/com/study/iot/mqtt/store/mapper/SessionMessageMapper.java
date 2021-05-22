package com.study.iot.mqtt.store.mapper;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import com.study.iot.mqtt.store.hbase.TableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.ReflectionUtils;

import java.math.BigDecimal;
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
    private final static byte[] COLUMN_FAMILY = SessionMessage.COLUMN_FAMILY.getBytes();

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

    @Override
    public List<Mutation> mutations(SessionMessage message) throws Exception {
        // rowKey
        Put put = new Put(Bytes.toBytes(message.getRow()));
        // 列族，列名，值
        ReflectionUtils.doWithFields(message.getClass(), field -> {
            field.setAccessible(true);
            String typeName = field.getGenericType().getTypeName();
            Object filedValue = field.get(message);
            // 如果不为空
            if (!ObjectUtil.isNull(filedValue)) {
                put.addColumn(Bytes.toBytes(SessionMessage.COLUMN_FAMILY), Bytes.toBytes(field.getName()),
                        convert(filedValue, typeName));
            }
        });
        // 返回
        return Lists.newArrayList(put);
    }

    /**
     * 类型转换
     *
     * @param fieldValue 值
     * @param type       类型
     * @return {@link byte[]}
     */
    private byte[] convert(Object fieldValue, String type) {
        if ("java.lang.String".equals(type)) {
            return Bytes.toBytes((String) fieldValue);
        } else if ("java.lang.Integer".equals(type) || "int".equals(type)) {
            return Bytes.toBytes((Integer) fieldValue);
        } else if ("java.lang.Boolean".equals(type)) {
            return Bytes.toBytes((Boolean) fieldValue);
        } else if ("byte[]".equals(type)) {
            return (byte[]) fieldValue;
        } else if ("java.sql.Timestamp".equals(type) || "java.util.Date".equals(type)) {
            return Bytes.toBytes((String) fieldValue);
        } else if ("java.lang.Long".equals(type) || "long".equals(type)) {
            return Bytes.toBytes((Long) fieldValue);
        } else if ("java.lang.Float".equals(type) || "float".equals(type)) {
            return Bytes.toBytes((Float) fieldValue);
        } else if ("java.math.BigDecimal".equals(type)) {
            return Bytes.toBytes((BigDecimal) fieldValue);
        } else {
            log.info("file value type: {}, value: {}", type, fieldValue);
            return Bytes.toBytes((String) fieldValue);
        }
    }
}
