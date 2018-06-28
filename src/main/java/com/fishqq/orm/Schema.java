package com.fishqq.orm;

import java.util.Collection;

import com.google.common.base.CaseFormat;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/11
 */
public interface Schema {
    default String name() {
        String className = this.getClass().getSimpleName();
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
    }

    Collection<Column> columns();
    String columnsSql();
    String equalsSql();
    String paramsSql();
}