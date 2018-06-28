package com.fishqq.biz.schemas;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.Collection;
import java.util.List;

import com.fishqq.orm.BaseSchema;
import com.fishqq.orm.Column;
import com.google.common.collect.Lists;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/11
 */
public class User extends BaseSchema {
    public enum Columns implements Column{
        id(JDBCType.BIGINT),
        source_user_id(JDBCType.VARCHAR),
        source_type(JDBCType.VARCHAR),
        account_name(JDBCType.VARCHAR),
        nickname(JDBCType.VARCHAR),
        real_name(JDBCType.VARCHAR),
        display_name(JDBCType.VARCHAR),
        current_tenant_id(JDBCType.BIGINT),
        gmt_create(JDBCType.TIMESTAMP),
        gmt_modified(JDBCType.TIMESTAMP);

        private SQLType type;

        Columns(SQLType type) {
            this.type = type;
        }

        @Override
        public SQLType type() {
            return this.type;
        }
    }

    @Override
    public String name() {
        return "od_" + super.name();
    }

    @Override
    public Collection<Column> columns() {
        List<Column> columns = Lists.newArrayList();
        for (Columns cls : Columns.values()) {
            columns.add(cls);
        }

        return columns;
    }
}
