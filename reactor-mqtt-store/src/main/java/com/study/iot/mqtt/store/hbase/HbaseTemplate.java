package com.study.iot.mqtt.store.hbase;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */

@Slf4j
public class HbaseTemplate implements HbaseOperations {

    private volatile Connection connection;

    public HbaseTemplate(@NotNull Configuration configuration) {
        try {
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 60L,
                TimeUnit.SECONDS, new SynchronousQueue<>(), DefaultThreadFactory.forName("Hbase"),
                    new ThreadPoolExecutor.AbortPolicy());
            // init pool
            poolExecutor.prestartCoreThread();
            // 获取连接
            this.connection = ConnectionFactory.createConnection(configuration, poolExecutor);
        } catch (IOException e) {
            log.error("hbase connection资源池创建失败");
        }
    }

    @Override
    public <T> T execute(@NotBlank String tableName, @NotNull TableCallback<T> action) {
        try (Table table = this.connection.getTable(TableName.valueOf(tableName))) {
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HbaseSystemException(throwable);
        }
    }

    @Override
    public <T> List<T> find(String tableName, String family, final TableMapper<T> action) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addFamily(Bytes.toBytes(family));
        return this.find(tableName, scan, action);
    }

    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, final TableMapper<T> action) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(tableName, scan, action);
    }

    @Override
    public <T> List<T> find(String tableName, final Scan scan, final TableMapper<T> action) {
        return this.execute(tableName, table -> {
            int caching = scan.getCaching();
            // 如果caching未设置(默认是1)，将默认配置成5000
            if (caching == 1) {
                scan.setCaching(5000);
            }

            try (ResultScanner scanner = table.getScanner(scan)) {
                List<T> results = Lists.newArrayList();
                int rowNum = 0;
                for (Result result : scanner) {
                    results.add(action.mapRow(result, rowNum++));
                }
                return results;
            }
        });
    }

    @Override
    public <T> T get(String tableName, String rowName, final TableMapper<T> mapper) {
        return this.get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, final TableMapper<T> mapper) {
        return this.get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(String tableName, final String rowName, final String familyName, final String qualifier,
                     final TableMapper<T> mapper) {
        return this.execute(tableName, table -> {
            Get get = new Get(Bytes.toBytes(rowName));
            if (StringUtils.isNotBlank(familyName)) {
                byte[] family = Bytes.toBytes(familyName);
                if (StringUtils.isNotBlank(qualifier)) {
                    get.addColumn(family, Bytes.toBytes(qualifier));
                } else {
                    get.addFamily(family);
                }
            }
            Result result = table.get(get);
            return mapper.mapRow(result, 0);
        });
    }

    @Override
    public void execute(@NotBlank String tableName, @NotNull MutatorCallback action) {
        BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
        try (BufferedMutator mutator = this.connection.getBufferedMutator(mutatorParams.writeBufferSize(3 * 1024 * 1024))) {
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            log.error("error: {}", throwable.getMessage());
            throw new HbaseSystemException(throwable);
        }
    }

    @Override
    public <T> void saveOrUpdate(String tableName, T value, TableMapper<T> mapper) {
        try {
            this.saveOrUpdate(tableName, mapper.mutations(value));
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
            throw new HbaseSystemException(e);
        }
    }

    /**
     * 保存对象
     *
     * @param tableName 表名
     * @param mutations {@link Mutation}
     */
    private void saveOrUpdate(String tableName, final List<Mutation> mutations) {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutations);
        });
    }
}
