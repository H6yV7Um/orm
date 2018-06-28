/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */

package com.fishqq.orm.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/1/11
 */
public class JdbcDataSource {
    private static final Logger logger = LoggerFactory.getLogger(JdbcDataSource.class);

    private int timeoutSeconds;
    private String driverClass;
    private String userName;
    private String password;
    private String url;

    public JdbcDataSource(String driverClass, String url, String userName, String password, int timeoutSeconds) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.driverClass = driverClass;

        this.timeoutSeconds = timeoutSeconds;

        DriverManager.setLoginTimeout(timeoutSeconds);

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection open() throws SQLException {
        try {
            return DriverManager.getConnection(this.url, this.userName, this.password);
        } catch (SQLException e) {
            logger.error("open jdbc connection exception: " + this.toString(), e);
            throw e;
        }
    }

    public void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("close jdbc connection error: " + this.toString(), e);
            }
        }
    }

    @Override
    public String toString() {
        return "JdbcDataSource{" +
            ", driverClass='" + driverClass + '\'' +
            ", url='" + url + '\'' +
            ", userName='" + userName + '\'' +
            ", password='" + password + '\'' +
            "timeoutSeconds=" + timeoutSeconds +
            '}';
    }


}
