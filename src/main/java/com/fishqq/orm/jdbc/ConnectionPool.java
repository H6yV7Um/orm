package com.fishqq.orm.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/19
 */
public interface ConnectionPool {
    JdbcDataSource dataSource();
    Connection get() throws SQLException;
    void put(Connection connection);
}
