package com.fishqq.orm;

import java.sql.SQLType;

import com.google.common.base.CaseFormat;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/11
 */
public interface Column {
    String name();

    SQLType type();

    default String field() {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }
}
