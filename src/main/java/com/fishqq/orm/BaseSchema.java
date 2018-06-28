package com.fishqq.orm;

import com.fishqq.orm.sql.Builder;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/11
 */
public abstract class BaseSchema implements Schema {
    private String columnsSql;
    private String equalsSql;
    private String paramsSql;

    @Override
    public String columnsSql() {
        if (columnsSql == null) {
            columnsSql = Builder.create().appendColumns(this.columns()).build();
        }

        return this.columnsSql;
    }

    @Override
    public String equalsSql() {
        if (equalsSql == null) {
            this.equalsSql = Builder.create().appendJoin(
                this.columns().stream()
                    .map(column -> Builder.create().equalParam(column.name(), column.field()).build()))
                .build();
        }

        return this.equalsSql;
    }

    @Override
    public String paramsSql() {
        if (this.paramsSql == null) {
            this.equalsSql = Builder.create().appendJoin(
                this.columns().stream().map(column -> Builder.asParam(column.field()))).build();
        }

        return this.paramsSql;
    }
}
