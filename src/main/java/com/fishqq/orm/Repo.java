package com.fishqq.orm;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fishqq.orm.jdbc.ConnectionPool;
import com.fishqq.orm.jdbc.SqlExecutor;
import com.fishqq.orm.jdbc.SqlExecutor.ResultSetHandler;
import com.fishqq.orm.sql.Builder;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/19
 */
public class Repo {
    private ConnectionPool connectionPool;

    public Repo(ConnectionPool pool) {
        this.connectionPool = pool;
    }

    public static <T> ResultSetHandler<List<T>> beanReader(Class<T> type) {
        return (rs) -> ResultSetReader.readToBean(rs, type);
    }

    public static final ResultSetHandler<List<Record>> RecordReader = ResultSetReader::readToRecord;

    public int insert(Schema schema, Map<String, Object> params) {
        String sql = Builder.insert(schema, params);

        return SqlExecutor.executeUpdate(this.connectionPool, sql);
    }

    public int insert(String table, Iterable<Object> columns, Object ...params) {
        String sql = Builder.insert(table, StreamSupport.stream(columns.spliterator(), false), Stream.of(params));

        return SqlExecutor.executeUpdate(this.connectionPool, sql);
    }

    public List<Record> all(Schema schema) {
        String sql = Builder.selectAll(schema);

        List<Record> records = SqlExecutor.executeQuery(this.connectionPool, RecordReader, sql);

        records.stream().forEach(record -> record.setSchema(schema.name()));

        return records;
    }

    public <T> List<T> all(Schema schema, Class<T> type) {
        String sql = Builder.selectAll(schema);

        List<T> records = SqlExecutor.executeQuery(this.connectionPool, beanReader(type), sql);

        return records;
    }

    public boolean execute(String sql) {
        return SqlExecutor.execute(this.connectionPool, sql);
    }

    public int update(Schema schema, Map<String, Object> params) {
        String sql = Builder.update(schema, params, true);

        return SqlExecutor.executeUpdate(this.connectionPool, sql);
    }

    public List<Record> queryToRecord(String sql) {
        return SqlExecutor.executeQuery(this.connectionPool, RecordReader, sql);
    }

    public <T> List<T> queryToBean(String sql, Class<T> type) {
        return SqlExecutor.executeQuery(this.connectionPool, beanReader(type), sql);
    }
}
