package com.study.iot.mqtt.store.hbase;

import org.apache.hadoop.hbase.client.Result;

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
}
