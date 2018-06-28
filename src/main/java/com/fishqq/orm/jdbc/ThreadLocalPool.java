package com.fishqq.orm.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/19
 */
public class ThreadLocalPool implements ConnectionPool {
    private JdbcDataSource jdbcDataSource;

    private ThreadLocal<Connection> connectionThreadLocal;

    public ThreadLocalPool(JdbcDataSource dataSource) {
        this.jdbcDataSource = dataSource;
        this.connectionThreadLocal = new ThreadLocal<>();
    }

    @Override
    public JdbcDataSource dataSource() {
        return this.jdbcDataSource;
    }

    @Override
    public Connection get() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection == null) {
            connection = jdbcDataSource.open();
            connectionThreadLocal.set(connection);
        }

        return connection;
    }

    @Override
    public void put(Connection connection) {

    }
}
