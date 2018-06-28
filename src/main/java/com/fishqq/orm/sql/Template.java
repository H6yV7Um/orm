package com.fishqq.orm.sql;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fishqq.orm.sql.Parser.Element;
import com.fishqq.orm.sql.Parser.Expr;
import com.fishqq.orm.sql.Parser.Key;
import com.fishqq.orm.sql.Parser.Value;
import com.google.common.collect.Maps;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/27
 */
public class Template {
    private String template;
    private String sql;

    private List<Element> es;
    private Map<String, String> keyValue = Maps.newHashMap();

    private Template(String t) {
        this.template = t;
    }

    /**
     * select <columns> from <table>
     * join item on user.id = item.user_id
     * <where user.id = :userId>
     * <and item.id = :itemId>
     * <or user.id in (<us>)>
     *
     * @param template
     * @return
     */
    public static Template create(String template) {
        Template sql = new Template(template);
        sql.es = new Parser(template).parse();

        return sql;
    }

    public Template renderVar(String key, Object ...exprs) {
        this.keyValue.put(key, Builder.buildExprList(exprs));
        return this;
    }

    public Template renderVar(String key, Iterable<Object> exprs) {
        this.keyValue.put(key, Builder.buildExprList(exprs));
        return this;
    }

    public Template renderVar(String key, Stream<Object> exprs) {
        this.keyValue.put(key, Builder.buildExprList(exprs));
        return this;
    }

    public Template renderParam(String key, Object ...values) {
        this.keyValue.put(key, Builder.buildValueList(values));
        return this;
    }

    public Template renderParam(String key, Iterable<Object> values) {
        this.keyValue.put(key, Builder.buildValueList(values));
        return this;
    }

    public Template renderExpr(String key, Object ...values) {
        return this.renderExpr(true, key, values);
    }

    public Template renderExpr(String key, Iterable<Object> values) {
        return this.renderExpr(true, key, values);
    }

    public Template renderExpr(boolean condition, String key, Object ...values) {
        return this.renderExpr(condition, key, Arrays.asList(values));
    }

    public Template renderExpr(boolean condition, String key, Iterable<Object> values) {
        this.keyValue.put(key, Builder.buildValueList(values));

        for (Element e : this.es) {
            if (e instanceof Expr) {
                Expr expr = (Expr) e;
                if (expr.values.size() == 1 && expr.values.get(0).param.equals(key)) {
                    expr.condition = condition;
                }
            }
        }

        return this;
    }

    public Template renderExpr(boolean condition) {
        // condition of expr without params
        for (Element e : this.es) {
            if (e instanceof Expr) {
                Expr expr = (Expr) e;
                if (expr.values.isEmpty()) {
                    expr.condition = condition;
                }
            }
        }

        return this;
    }

    /**
     * select id from user
     * order by <order_by>
     *
     * int order = 1;
     *
     * Template.create(sql)
     * .icase(order, "order_by")
     * .when(1, "name")
     * .when(2, "gmt_create")
     * .when(3, "score + grade")
     * .idefault("name");
     *
     * @param var
     * @param keyOfExpr
     * @return
     */
    public Template icase(Object var, String keyOfExpr, boolean isVar) {
        return this;
    }

    public Template when(Object varValue, Object paramValue) {
        return this;
    }

    public Template idefault(Object paramValue) {
        return this;
    }

    // todo
    public Template bind(Object ...values) {
        return this;
    }

    public Template bind(int i, Object value) {
        return this;
    }

    public String toSql() {
        if (this.sql != null) {
            return this.sql;
        }

        if (this.es.isEmpty()) {
            this.sql = this.template;
            return this.sql;
        }

        StringBuilder str = new StringBuilder();

        int i = 0;

        for (Element element : this.es) {
            str.append(this.template.substring(i, element.start));

            if (element instanceof Expr) {
                Expr expr = (Expr) element;
                if (expr.condition) {
                    int exprIndex = expr.start + 1;

                    for (Value value : expr.values) {
                        str.append(this.template.substring(exprIndex, value.start));
                        str.append(this.keyValue.get(value.param));
                        exprIndex = value.end;
                    }

                    if (expr.values.size() > 0) {
                        int remainStart = expr.values.get(expr.values.size() - 1).end;
                        int remainEnd = expr.end - 1; // last >
                        if (remainEnd > remainStart) {
                            str.append(this.template.substring(remainStart, remainEnd));
                        }
                    }
                }
            } else if (element instanceof Key) {
                Key key = (Key) element;
                str.append(this.keyValue.get(key.param));
            } else if (element instanceof Value) {
                Value value = (Value) element;
                str.append(this.keyValue.get(value.param));
            }

            i = element.end;
        }

        str.append(this.template.substring(i));

        this.sql = str.toString();

        return this.sql;
    }

    @Override
    public String toString() {
        return this.sql;
    }

    public static void main(String[] args) {
        String template = "select <columns> from <table>\n"
            + "      join item on user.id = item.user_id\n"
            + "     where user.id = :userId\n"
            + "     <and item.id = :itemId>\n"
            + "     <or user.id in (:userIds)>";

        Integer itemId = 1;
        List<Integer> userIds = Arrays.asList(1,2,3);

        Template sql = Template.create(template)
            .renderVar("columns", "id", "name")
            .renderVar("table", "user")
            .renderParam("userId", 1)
            .renderExpr(itemId != null, "itemId", 2)
            .renderExpr(userIds != null && !userIds.isEmpty(), "userIds", 1, 2, 3);

        System.out.println(sql.toSql());
    }
}
