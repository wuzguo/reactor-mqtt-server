package com.study.iot.mqtt.store.hbase;

import com.study.iot.mqtt.common.domain.BaseMessage;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Scan;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */

public interface HbaseOperations {

    /**
     * Executes the given action against the specified table handling resource management.
     * <p>
     * Application exceptions thrown by the action object get propagated to the caller (can only be unchecked). Allows
     * for returning a result object (typically a domain object or collection of domain objects).
     *
     * @param tableName the target table
     * @param <T>       action type
     * @return the result object of the callback action, or null
     */
    <T> T execute(@NotBlank String tableName, @NotNull TableCallback<T> action);

    /**
     * Scans the target table, using the given column family. The content is processed row by row by the given action,
     * returning a list of domain objects.
     *
     * @param tableName target table
     * @param family    column family
     * @param <T>       action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, String family, final RowMapper<T> mapper);

    /**
     * Scans the target table, using the given column family. The content is processed row by row by the given action,
     * returning a list of domain objects.
     *
     * @param tableName target table
     * @param family    column family
     * @param qualifier column qualifier
     * @param <T>       action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, String family, String qualifier, final RowMapper<T> mapper);

    /**
     * Scans the target table using the given {@link Scan} object. Suitable for maximum control over the scanning
     * process. The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param scan      table scanner
     * @param <T>       action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName target table
     * @param rowName   row name
     * @param mapper    row mapper
     * @param <T>       mapper type
     * @return object mapping the target row
     */
    <T> T get(String tableName, String rowName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName column family
     * @param mapper     row mapper
     * @param <T>        mapper type
     * @return object mapping the target row
     */
    <T> T get(String tableName, String rowName, String familyName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName family
     * @param qualifier  column qualifier
     * @param mapper     row mapper
     * @param <T>        mapper type
     * @return object mapping the target row
     */
    <T> T get(String tableName, final String rowName, final String familyName, final String qualifier,
        final RowMapper<T> mapper);

    /**
     * 执行put update or delete
     *
     * @param tableName target table
     * @param action    {@link MutatorCallback}
     */
    void execute(@NotBlank String tableName, @NotNull MutatorCallback action);

    /**
     * @param tableName target table
     * @param mutation  {@link Mutation}
     */
    void saveOrUpdate(String tableName, Mutation mutation);

    /**
     * @param tableName target table
     * @param mutations {@link Mutation}
     */
    void saveOrUpdates(String tableName, List<Mutation> mutations);

    /**
     * 保存对象
     *
     * @param tableName 表名称
     * @param message   消息体 {@link BaseMessage}
     */
    void saveOrUpdate(String tableName, BaseMessage message);
}