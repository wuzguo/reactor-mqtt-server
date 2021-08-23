package com.study.iot.mqtt.store.mapper;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.message.RetainMessage;
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
 * @date 2021/8/23 15:50
 */

@Slf4j
public class RetainMessageMapper implements TableMapper<RetainMessage> {

    /**
     * 列族
     */
    private final static byte[] COLUMN_FAMILY = RetainMessage.COLUMN_FAMILY.getBytes(StandardCharsets.UTF_8);

    /**
     * ROW
     */
    private final static byte[] ROW = "row".getBytes(StandardCharsets.UTF_8);

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
     * 是否重发
     */
    private final static byte[] DUP = "dup".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息
     */
    private final static byte[] COPY_BYTE_BUF = "copyByteBuf".getBytes(StandardCharsets.UTF_8);


    @Override
    public RetainMessage mapRow(Result result, int rowNum) throws Exception {
        return RetainMessage.builder()
            .row(Bytes.toString(result.getValue(COLUMN_FAMILY, ROW)))
            .topic(Bytes.toString(result.getValue(COLUMN_FAMILY, TOPIC)))
            .retain(Boolean.getBoolean(Bytes.toString(result.getValue(COLUMN_FAMILY, RETAIN))))
            .qos(Bytes.toInt(result.getValue(COLUMN_FAMILY, QOS)))
            .dup(Boolean.getBoolean(Bytes.toString(result.getValue(COLUMN_FAMILY, DUP))))
            .copyByteBuf(result.getValue(COLUMN_FAMILY, COPY_BYTE_BUF))
            .build();
    }

    @Override
    public List<Mutation> mutations(RetainMessage message) throws Exception {
        // rowKey
        Put put = new Put(Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(RetainMessage.COLUMN_FAMILY), ROW, Bytes.toBytes(message.getRow()));
        put.addColumn(Bytes.toBytes(RetainMessage.COLUMN_FAMILY), TOPIC, Bytes.toBytes(message.getTopic()));
        put.addColumn(Bytes.toBytes(RetainMessage.COLUMN_FAMILY), RETAIN, Bytes.toBytes(message.getRetain()));
        put.addColumn(Bytes.toBytes(RetainMessage.COLUMN_FAMILY), QOS, Bytes.toBytes(message.getQos()));
        put.addColumn(Bytes.toBytes(RetainMessage.COLUMN_FAMILY), DUP, Bytes.toBytes(message.getDup()));
        put.addColumn(Bytes.toBytes(RetainMessage.COLUMN_FAMILY), COPY_BYTE_BUF, message.getCopyByteBuf());
        return Lists.newArrayList(put);
    }
}
