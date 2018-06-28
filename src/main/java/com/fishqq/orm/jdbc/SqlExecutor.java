package com.fishqq.orm.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jdbc sql执行器
 *
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/1/22
 */
public class SqlExecutor {
    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);

    public static <T> T withConnection(ConnectionPool pool, ConnectionHanlder<T> handler) {
        Connection connection = null;

        try {
            connection = pool.get();
        } catch (SQLException e) {
            logger.error("get sql connection exception from : " + pool.dataSource(), e);
            throw new RuntimeException(e);
        }

        try {
            return handler.handle(connection);
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException e1) {
                logger.error("close connection exception", e);
            }

            logger.error("handle connection exception", e);

            return null;
        }
    }

    public static <T> T withPreparedStatement(ConnectionPool connectionPool,
                                              PreparedStatementHandler<T> handler,
                                              ParamSetter paramSetter,
                                              String sql)  {
        return withConnection(connectionPool, (Connection connection) -> {
            String actualSql = sql;
            PreparedStatement preparedStatement = null;

            try {
                preparedStatement = connection.prepareStatement(sql);
                paramSetter.set(preparedStatement);

                // postgres mysql可以获取sql，其他驱动需要自己手写
                actualSql = preparedStatement.toString();

                long start = System.currentTimeMillis();

                T result = handler.handle(preparedStatement);

                long end = System.currentTimeMillis();

                logger.debug("spend {} ms execute sql: {}", end - start, actualSql);

                return result;
            } catch (Throwable e) {
                if (preparedStatement == null) {
                    logger.error("prepare statement exception, sql create: " + sql, e);
                } else {
                    logger.error("execute sql exception, sql: " + actualSql, e);
                }

                return null;
            }
        });
    }

    public static <T> T withPreparedStatement(ConnectionPool connectionPool,
                                              PreparedStatementHandler<T> handler,
                                              String sql)  {
        return withPreparedStatement(connectionPool, handler, EmptyParamSetter.INSTANE, sql);
    }

    public static boolean execute(ConnectionPool connectionPool, String sql)  {
        Boolean result = withPreparedStatement(connectionPool, stat -> stat.execute(), sql);
        return Optional.ofNullable(result).orElse(false);
    }

    public static int executeUpdate(ConnectionPool connectionPool,
                                    String sql) {
        return executeUpdate(connectionPool, EmptyParamSetter.INSTANE, sql);
    }

    public static int executeUpdate(ConnectionPool connectionPool,
                                    ParamSetter paramSetter,
                                    String sql)  {
        Integer result = withPreparedStatement(connectionPool, stat -> stat.executeUpdate(), paramSetter, sql);
        return Optional.ofNullable(result).orElse(0);
    }

    public static ResultSet executeQuery(ConnectionPool connectionPool,
                                         ParamSetter paramSetter,
                                         String sql)  {
        ResultSet result = withPreparedStatement(connectionPool, stat -> stat.executeQuery(), paramSetter, sql);
        return Optional.ofNullable(result).orElse(EmptyResultSet.instance);
    }

    public static <T> T executeQuery(ConnectionPool connectionPool,
                                     ParamSetter paramSetter,
                                     ResultSetHandler<T> handler,
                                     String sql)  {
        try (ResultSet resultSet = executeQuery(connectionPool, paramSetter, sql)) {
            return handler.handle(resultSet);
        } catch (SQLException e) {
            logger.error("close result set exception,datasource: " + connectionPool.dataSource() + ", sql: " + sql, e);
            return null;
        }
    }

    public static <T> T executeQuery(ConnectionPool connectionPool,
                                     ResultSetHandler<T> handler,
                                     String sql)  {
        return executeQuery(connectionPool, EmptyParamSetter.INSTANE, handler, sql);
    }

    public static void setParam(PreparedStatement stat, int i, Object param) throws SQLException {
        if (param instanceof Boolean) {stat.setBoolean(i, (Boolean)param);}
        if (param instanceof Byte) {stat.setByte(i, (Byte)param);}
        if (param instanceof Short) {stat.setShort(i, (Short)param);}
        if (param instanceof Integer) {stat.setInt(i, (Integer)param);}
        if (param instanceof Long) {stat.setLong(i, (Long)param);}
        if (param instanceof Float) {stat.setFloat(i, (Float)param);}
        if (param instanceof Double) {stat.setDouble(i, (Double)param);}
        if (param instanceof BigDecimal) {stat.setBigDecimal(i, (BigDecimal)param);}
        if (param instanceof String) {stat.setString(i, (String)param);}
        if (param instanceof byte[]) {stat.setBytes(i, (byte[])param);}
        if (param instanceof java.sql.Date) {stat.setDate(i, (java.sql.Date)param);}
        if (param instanceof java.util.Date) {
            stat.setTimestamp(i, new java.sql.Timestamp(((java.util.Date)param).getTime()));
        }
        if (param instanceof java.sql.Time) {stat.setTime(i, (java.sql.Time)param);}

        if (param instanceof java.sql.Timestamp) {stat.setTimestamp(i, (java.sql.Timestamp)param);}
        //if (param instanceof java.io.InputStream) {stat.setAsciiStream(i, (java.io.InputStream)param);}
        if (param instanceof java.io.InputStream) {stat.setBinaryStream(i, (java.io.InputStream)param);}
        if (param instanceof java.util.UUID) {stat.setObject(i, param);}
        if (param instanceof java.io.Reader) {stat.setCharacterStream(i, (java.io.Reader)param);}
        //if (param instanceof java.io.Reader) {stat.setNCharacterStream(i, (java.io.Reader)param);}
        if (param instanceof java.sql.Ref) {stat.setRef(i, (java.sql.Ref)param);}
        if (param instanceof java.sql.Blob) {stat.setBlob(i, (java.sql.Blob)param);}
        if (param instanceof java.sql.Clob) {stat.setClob(i, (java.sql.Clob)param);}
        if (param instanceof java.sql.Array) {stat.setArray(i, (java.sql.Array)param);}
        if (param instanceof java.net.URL) {stat.setURL(i, (java.net.URL)param);}
        if (param instanceof java.sql.RowId) {stat.setRowId(i, (java.sql.RowId)param);}
        if (param instanceof String) {stat.setNString(i, (String)param);}
        if (param instanceof java.sql.NClob) {stat.setNClob(i, (java.sql.NClob)param);}
        if (param instanceof java.sql.SQLXML) {stat.setSQLXML(i, (java.sql.SQLXML)param);}
    }

    @FunctionalInterface
    public interface ConnectionHanlder<T> {
        T handle(Connection connection) throws SQLException;
    }

    /**
     * 处理sql语句
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface PreparedStatementHandler<T> {
        /**
         * doHandle
         *
         * @param statement
         * @return
         * @
         */
        T handle(PreparedStatement statement) throws SQLException;
    }

    /**
     * 设置参数
     */
    @FunctionalInterface
    public interface ParamSetter {
        /**
         * set
         *
         * @param preparedStatement
         * @
         */
        void set(PreparedStatement preparedStatement);
    }

    /**
     * 处理结果
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface ResultSetHandler<T> {
        /**
         * doHandle
         *
         * @param resultSet
         * @return
         * @
         */
        T handle(ResultSet resultSet);
    }

    public static class EmptyParamSetter implements ParamSetter {
        private EmptyParamSetter() {}

        public static final EmptyParamSetter INSTANE = new EmptyParamSetter();

        @Override
        public void set(PreparedStatement statement) {}
    }
}
