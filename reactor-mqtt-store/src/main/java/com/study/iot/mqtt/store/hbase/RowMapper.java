package com.study.iot.mqtt.store.hbase;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;

import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */

public interface RowMapper<T> {

    /**
     * Mapper
     *
     * @param result {@link Result}
     * @param rowNum {@link Integer}
     * @return {@link T}
     * @throws Exception
     */
    T mapRow(Result result, int rowNum) throws Exception;

    /**
     * 将对象转换为 Mutation
     *
     * @param value 对象
     * @return {@link Mutation}
     * @throws Exception
     */
    List<Mutation> mapObject(T value) throws Exception;
}
