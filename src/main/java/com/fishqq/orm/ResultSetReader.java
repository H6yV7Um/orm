package com.fishqq.orm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/25
 */
public class ResultSetReader {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetReader.class);

    public static <R, T> List<R> read(ResultSet rs, RowHandler<R> rh, ValueHandler<R, T> vh, int limit) {
        List<R> result = Lists.newArrayList();

        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();

            Map<String, Integer> name2Index = Maps.newHashMapWithExpectedSize(columns);

            for (int i = 0; i < columns; i++) {
                name2Index.put(meta.getColumnName(i + 1), i);
            }

            int readed = 0;
            while (rs.next() && readed < limit) {
                R r = rh.handle(rs, readed, name2Index);

                for (int i = 0; i < columns; i++) {
                    T t = vh.handle(i, columns, meta.getColumnName(i + 1), rs.getObject(i + 1), r);
                }

                result.add(r);

                readed++;
            }
        } catch (SQLException e) {
            logger.error("read result set error", e);
            throw new RuntimeException(e);
        }

        return result;
    }

    public static List<Map<String, Object>> readToMap(ResultSet rs, int limit) {
        RowHandler<Map<String, Object>> rh = (r, rowIndex, name2Index) ->
            Maps.newHashMapWithExpectedSize(name2Index.size());

        ValueHandler<Map<String, Object>, Void> vh = (i, columnCount, columName, value, rowMap) -> {
            rowMap.put(columName, value);
            return null;
        };

        List<Map<String, Object>> result = read(rs, rh, vh, limit);

        return result;
    }

    public static List<Map<String, Object>> readToMap(ResultSet rs) {
        return readToMap(rs, Integer.MAX_VALUE);
    }

    public static Map<String, Object> readFirstToMap(ResultSet rs) {
        return first(readToMap(rs, 1));
    }

    public static List<Record> readToRecord(ResultSet rs, int limit) {
        RowHandler<Record> rh = (r, rowIndex, name2Index) -> new Record(name2Index);

        ValueHandler<Record, Void> vh = (i, columnCount, columName, value, record) -> {
            record.put(i, value);
            return null;
        };

        List<Record> result = read(rs, rh, vh, limit);

        return result;
    }

    public static List<Record> readToRecord(ResultSet rs) {
        return readToRecord(rs, Integer.MAX_VALUE);
    }

    public static Record readFirstToRecord(ResultSet rs) {
        return first(readToRecord(rs, 1));
    }


    public static <R> List<R> readToBean(ResultSet rs, Class<R> type) {
        return readToBean(rs, type, Integer.MAX_VALUE);
    }

    public static <R> R readFirstToBean(ResultSet rs, Class<R> type) {
        List<R> result = readToBean(rs, type, 1);
        return first(result);
    }

    public static <R> List<R> readToBean(ResultSet rs, Class<R> type, int limit) {
        RowHandler<R> rh = (r, rowIndex, name2Index) -> {
            try {
                return type.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("can't create instance of type {}", type.toString());
                throw new RuntimeException(e);
            }
        };

        ValueHandler<R, Void> vh = (i, columnCount, columName, value, bean) -> {
            String setterName = "set" + getUpperField(columName);

            Method setMethod = null;

            // 兼容bool类型的方法设置isXX -> setXX/setIsXX
            if (value instanceof Boolean) {
                setMethod = getSetter(bean, setterName, false);
                if (setMethod == null) {
                    String field = getUpperField(columName);
                    // remove "is"
                    if (!field.startsWith("Is")) {
                        throw new RuntimeException(
                            "boolean field don't start with \"is\": " + bean.getClass().getName() + "." + field);
                    }
                    setterName = "set" + field.substring("is".length());
                    setMethod = getSetter(bean, setterName, true);
                }
            } else {
                setMethod = getSetter(bean, setterName, true);
            }

            try {
                setMethod.invoke(bean, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("call setter method exception: {} {}", bean.getClass().getName(), setterName);
                throw new RuntimeException(e);
            }

            return null;
        };

        List<R> result = read(rs, rh, vh, limit);

        return result;
    }

    private static String getUpperField(String columnName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, columnName);
    }

    private static <T> Method getSetter(T bean, String setterName, boolean required) {
        try {
            return bean.getClass().getMethod(setterName, bean.getClass());
        } catch (NoSuchMethodException e) {
            if (required) {
                String msg = String.format("can't find method %s of %s", setterName, bean.getClass().getName());
                logger.error(msg, e);
                throw new RuntimeException(msg, e);
            } else {
                return null;
            }
        }
    }

    public static <T> T first(Collection<T> es) {
        return es == null || es.isEmpty() ? null : es.iterator().next();
    }

    @FunctionalInterface
    public interface RowHandler<R> {
        R handle(ResultSet rs, int rowIndex, Map<String, Integer> name2Index) throws SQLException;
    }

    @FunctionalInterface
    public interface ValueHandler<R, T> {
        T handle(int i, int columnCount, String columnName, Object object, R rowResult);
    }
}
