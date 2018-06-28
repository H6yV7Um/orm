package com.fishqq.orm.sql;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/28
 */
class Parser {
    private String template;

    Parser(String template) {
        this.template = template;
    }

    List<Element> parse() {
        List<Element> es = Lists.newArrayList();

        for (int i = 0; i < this.template.length(); ) {
            char c = this.template.charAt(i);

            // key or expr
            if (c == '\'') {
                i = this.skipTo(i, '\'') + 1;
            } else if (c == '"') {
                i = this.skipTo(i, '"') + 1;
            } else if (c == '-') {
                if (i < this.template.length()) {
                    c = this.template.charAt(i++);
                    if (c == '-') {
                        i = this.skipTo(i, '\n') + 1;
                    } else {
                        i++;
                    }
                } else {
                    i++;
                }
            } else if (c == '<') {
                Element e = this.parseKeyOrExpr(i);
                es.add(e);

                i = e.end;
            }
            // maybe value
            else if (c == ':') {
                if (c < this.template.length()) {
                    char next = this.template.charAt(i + 1);
                    if (isParamChar(next)) {
                        Value v = this.parseValue(i);
                        es.add(v);

                        i = v.end;
                    } else {
                        i++;
                    }
                } else {
                    i++;
                }

            } else {
                i++;
            }
        }

        return es;
    }

    private int skipTo(int start, char c) {
        int i = start;

        while (i < this.template.length() && this.template.charAt(i) != c) {
            i++;
        }

        return i;
    }

    private Value parseValue(int start) {
        int i = start;

        // skip ':'
        i++;

        char c = this.template.charAt(i);
        while (i < this.template.length() && isParamChar(c)) {
            i++;
            c = this.template.charAt(i);
        }

        if (i == start) {
            throw new RuntimeException(String.format("bad sql create syntax at [%d, %d] of %s", start, i, template));
        }

        return new Value(start, i, template.substring(start + 1, i));
    }

    private static boolean isParamChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '-';
    }

    private Element parseKeyOrExpr(int start) {
        int i = start;

        // skip <
        i++;

        boolean containsBlank = false;

        List<Value> es = Lists.newArrayList();

        int count = -1;
        while (i < this.template.length() && count != 0) {
            char c = this.template.charAt(i);

            // todo <> escape
            if (c == '<') {
                count--;
            } else if (c == '>') {
                count++;
            } else if (c == ':') {
                Value value = this.parseValue(i);
                es.add(value);
                i = value.end - 1;
            } else if (Character.isWhitespace(c)) {
                containsBlank = true;
            }

            i++;
        }

        if (containsBlank && es.isEmpty()) {
            throw new RuntimeException(String.format("bad sql create syntax at [%d, %d] of %s", start, i, template));
        }

        // is expr
        if (containsBlank) {
            return new Expr(start, i, es);
        } else {
            return new Key(start, i, this.template.substring(start + 1, i - 1).trim());
        }
    }

    class Element {
        int start;
        int end;
    }

    class Value extends Element {
        String param;

        Value(int start, int end, String param) {
            this.start = start;
            this.end = end;
            this.param = param;
        }
    }

    class Key extends Element {
        String param;

        Key(int start, int end, String param) {
            this.start = start;
            this.end = end;
            this.param = param;
        }
    }

    class Expr extends Element {
        List<Value> values;
        boolean condition = true;

        Expr(int start, int end, List<Value> values) {
            this.start = start;
            this.end = end;
            this.values = values;
        }
    }
}
