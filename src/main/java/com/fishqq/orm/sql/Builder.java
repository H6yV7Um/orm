package com.fishqq.orm.sql;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fishqq.orm.Column;
import com.fishqq.orm.Schema;
import com.google.common.collect.Lists;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/11
 */
public class Builder {
    public static char space = ' ';
    public static String delimiter = ", ";
    public static String newline = "\n";

    protected StringBuilder sql = new StringBuilder();

    private Builder(String sqlPart) { }

    public static Builder create() {
        return new Builder("");
    }

    public static final String insert_template = "insert into <table>(<columns>) values(<params>)";

    public static String insert(Schema schema, Map<String, Object> params) {
        String[] columns = new String[params.size()];
        Object[] values = new Object[params.size()];

        int i = 0;
        for (Entry<String, Object> entry : params.entrySet()) {
            columns[i] = entry.getKey();
            values[i] = entry.getValue();
            i++;
        }

        return insert(schema.name(), columns, values);
    }

    public static String insert(String table, Iterable<Object> columns, Object ...params) {
        return insert(table, StreamSupport.stream(columns.spliterator(), false), Stream.of(params));
    }

    public static String insert(String table, Object[] columns, Object ...params) {
        return insert(table, Arrays.asList(columns), Arrays.asList(params));
    }

    public static String insert(String table, Stream<Object> columns, Stream<Object> params) {
        Template template = Template.create(insert_template)
            .renderVar("table", table)
            .renderVar("columns", columns)
            .renderParam("params", params);

        return template.toSql();
    }

    public static final String select_all_template = "select <columns> from <table>";

    public static String selectAll(Schema schema) {
        return Template.create(select_all_template)
            .renderExpr("columns", schema.columnsSql())
            .renderExpr("table", schema.name())
            .toSql();
    }

    public static final String update_template = "update <table> set <equalExpr>";

    public static String update(Schema schema, Map<String, Object> params, boolean withoutNull) {
        List<String> setExprs = Lists.newArrayList();

        for (Entry<String, Object> entry : params.entrySet()) {
            if (withoutNull && entry.getValue() == null) {
                continue;
            }

           setExprs.add(Builder.create().equalExprValue(entry.getKey(), entry.getValue()).toString());
        }

        if (setExprs.isEmpty()) {
            return Template.create(update_template).renderVar(schema.name(), "1=1").toSql();
        } else {
            return Template.create(update_template).renderVar(schema.name(), setExprs).toSql();
        }
    }

    public static Builder create(String sqlPart) {
        return new Builder(sqlPart);
    }

    public Builder appendColumnDelimiter() {
        return this.append(delimiter);
    }

    public Builder trimRight(String delimiter) {
        if (this.sql.length() >= delimiter.length()) {
            this.sql.setLength(this.sql.length() - delimiter.length());
        }
        return this;
    }

    public Builder trimLastColumnDelimiter() {
        return this.trimRight(delimiter);
    }

    public Builder appendJoin(String delimiter, Collection<String> es) {
        return this.append(String.join(delimiter, es));
    }

    public Builder appendJoin(Collection<String> es) {
        return this.appendJoin(delimiter, es);
    }

    public Builder appendJoin(Stream<String> es) {
        return this.appendJoin(es.collect(Collectors.toSet()));
    }

    public Builder appendColumns(String delimiter, Collection<Column> columns) {
        return this.appendJoin(delimiter, columns.stream().map(Column::name).collect(Collectors.toList()));
    }

    public Builder appendColumns(Collection<Column> columns) {
        return this.appendColumns(delimiter, columns);
    }

    public Builder append(String sqlPart) {
        if (sql.length() > 0 && sql.charAt(sql.length() - 1) != space) {
            sql.append(space);
        }

        sql.append(sqlPart);

        return this;
    }

    public Builder appendValue(Object value) {
        return this.append(buildValue(value));
    }

    public Builder appendValueList(Object... values) {
        return this.append(buildValueList(values));
    }

    public Builder appendValueList(Collection<Object> values) {
        return this.append(buildValueList(values));
    }

    public Builder binaryExpr(String left, String operator, String right) {
        this.append(left).append(operator).append(right);
        return this;
    }

    public Builder twoExpr(String leftExpr, String operator, String rightExpr) {
        return this.binaryExpr(leftExpr, operator, rightExpr);
    }

    public Builder exprValue(String expr, String operator, Object value) {
        return this.binaryExpr(expr, operator, buildValue(value));
    }

    public Builder twoValue(Object left, String operator, Object right) {
        return this.binaryExpr(buildValue(left), operator, buildValue(right));
    }

    public Builder exprValueNotNull(String expr, String operator, Object value) {
        return value == null ? this : exprValue(expr, operator, value);
    }

    public Builder equalExprValue(String expr, Object value) {
        return this.exprValue(expr, Operator.equal.operator(), value);
    }

    public Builder equalTwoExpr(String left, String right) {
        return this.twoExpr(left, Operator.equal.operator(), right);
    }

    public Builder notEqualExprValue(String expr, Object value) {
        return this.exprValue(expr, Operator.notEqual.operator(), value);
    }

    public Builder notEqualTwoExpr(String expr, String value) {
        return this.twoExpr(expr, Operator.notEqual.operator(), value);
    }

    public Builder equalParam(String column, String field) {
        return this.equalTwoExpr(column, asParam(field));
    }

    public Builder in(String column, Object... values) {
        if (values == null && values.length == 0) {
            return this;
        } else {
            return this.in(column, Arrays.asList(values));
        }
    }

    public Builder in(String column, Collection<Object> values) {
        if (values == null || values.isEmpty()) {
            return this;
        } else {
            return this.append(column).append(" in (").appendValueList(values).append(")");
        }
    }

    private boolean open = false;

    public Builder open(String str) {
        this.open = true;
        return this.append(str);
    }

    public Builder close(String str) {
        this.open = false;
        return this.append(str);
    }

    public static String asParam(String field) {
        return ":" + field;
    }

    public enum Operator {
        equal("="),
        notEqual("!="),
        like("like"),
        and("and"),
        or("or");

        private String operator;

        public String operator() {
            return this.operator;
        }

        Operator(String operator) {
            this.operator = operator;
        }
    }

    public static String buildExprList(Iterable<Object> exprs) {
        return buildExprList(StreamSupport.stream(exprs.spliterator(), false));
    }

    public static String buildExprList(Object ...exprs) {
        return buildExprList(Arrays.asList(exprs));
    }

    public static String buildExprList(Stream<Object> exprs) {
        return buildList(exprs, Builder::buildExpr);
    }

    public static String buildExpr(Object expr) {
        return expr.toString();
    }

    public static String buildValueList(Iterable<Object> values) {
        return buildValueList(StreamSupport.stream(values.spliterator(), false));
    }

    public static String buildValueList(Object ...values) {
        return buildValueList(Arrays.stream(values));
    }

    public static String buildValueList(Stream<Object> values) {
        return buildList(values, Builder::buildValue);
    }

    public static <T> String buildList(Stream<T> values, Function<Object, String> builder) {
        List<String> vs = values.map(builder).collect(Collectors.toList());
        return String.join(delimiter, vs);
    }

    // todo: other type with quote
    public static String buildValue(Object value) {
        if (value instanceof String) {
            return "'" + value.toString() + "'";
        } else if (value instanceof Date) {
            return buildValue(value.toString());
        } {
            return String.valueOf(value);
        }
    }

    public static String buildString(String str) {
        return "'" + str + "'";
    }

    public String build() {
        return sql.toString();
    }

}