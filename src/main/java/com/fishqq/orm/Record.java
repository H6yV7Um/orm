package com.fishqq.orm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.fishqq.orm.sql.Builder;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/19
 */
public class Record {
    private String schema;
    private Object[] values;
    private Map<String, Integer> name2Index;

    public Record(Map<String, Integer> name2Index) {
        this.values = new Object[name2Index.size()];
        this.name2Index = name2Index;
    }

    public Record(Object[] values, Map<String, Integer> name2Index) {
        this.values = values;
        this.name2Index = name2Index;
    }

    public Object get(String field) {
        if (!isInRange(field)) {
            return null;
        }

        return this.values[name2Index.get(field)];
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Object get(int index) {
        return this.values[index];
    }

    public Object[] getValues() {
        return this.values;
    }

    public void put(int index, Object value) {
        this.values[index] = value;
    }

    public void put(String field, Object value) {
        this.values[this.name2Index.get(field)] = value;
    }

    public String getString(String field) {
        return getValue(field, null);
    }

    public String getString(String field, String defaultValue) {
        return getValue(field, defaultValue);
    }

    public String getString(int index) {
        return getValue(index, null);
    }

    public String getString(int index, String defaultValue) {
        return getValue(index, defaultValue);
    }

    public Boolean getBool(String field) {
        return getValue(field, null);
    }

    public Boolean getBool(String field, Boolean defaultValue) {
        return getValue(field, defaultValue);
    }

    public Boolean getBool(int index) {
        return getValue(index, null);
    }

    public Boolean getBool(int index, Boolean defaultValue) {
        return getValue(index, defaultValue);
    }

    public Byte getByte(String field) {
        return getValue(field, null);
    }

    public Byte getByte(String field, Byte defaultValue) {
        return getValue(field, defaultValue);
    }

    public Byte getByte(int index) {
        return getValue(index, null);
    }

    public Byte getByte(int index, Byte defaultValue) {
        return getValue(index, defaultValue);
    }

    public Short getShort(String field) {
        return getShort(field, get(field, null));
    }

    public Short getShort(String field, short defaultValue) {
        return getShort(field, get(field, defaultValue));
    }

    public Short getShort(int index) {
        return getShort(String.valueOf(index), get(index, null));
    }
    public Short getShort(int index, short defaultValue) {
        return getShort(String.valueOf(index), get(index, defaultValue));
    }

    private Short getShort(String fieldOrIndex, Object value) {
        if (value instanceof Short) {
            return (Short) value;
        } else if (value instanceof Integer) {
            Integer intValue = (Integer) value;
            if (intValue <= Short.MAX_VALUE) {
                return intValue.shortValue();
            } else {
                throw new RuntimeException(String.format("column value(integer:%s) of %s too large for type short",
                    intValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof Long) {
            Long longValue = (Long) value;
            if (longValue <= Short.MAX_VALUE) {
                return longValue.shortValue();
            } else {
                throw new RuntimeException(String.format("column value(long:%s) of %s too large for type short",
                    longValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof BigInteger) {
            BigInteger bigIntegerValue = (BigInteger) value;
            if (bigIntegerValue.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) <= 0) {
                return bigIntegerValue.shortValue();
            } else {
                throw new RuntimeException(String.format("column value(BigInteger:%s) of %s too large for type short",
                    bigIntegerValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) value;
            if (bigDecimalValue.compareTo(BigDecimal.valueOf(Short.MAX_VALUE)) <= 0) {
                return bigDecimalValue.shortValue();
            } else {
                throw new RuntimeException(String.format("column value(BigDecimal:%s) of %s too large for type short",
                    bigDecimalValue.toString(), fieldOrIndex));
            }
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not number",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public Integer getInt(String field) {
        return getInt(field, get(field, null));
    }

    public Integer getInt(String field, int defaultValue) {
        return getInt(field, get(field, defaultValue));
    }

    public Integer getInt(int index) {
        return getInt(String.valueOf(index), get(index, null));
    }

    public Integer getInt(int index, int defaultValue) {
        return getInt(String.valueOf(index), get(index, defaultValue));
    }

    private Integer getInt(String fieldOrIndex, Object value) {
        if (value instanceof Short) {
            return Integer.valueOf((Short) value);
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            Long longValue = (Long) value;
            if (longValue <= Integer.MAX_VALUE) {
                return longValue.intValue();
            } else {
                throw new RuntimeException(String.format("column value(long:%s) of %s too large for type int",
                    longValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof BigInteger) {
            BigInteger bigIntegerValue = (BigInteger) value;
            if (bigIntegerValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
                return bigIntegerValue.intValue();
            } else {
                throw new RuntimeException(String.format("column value(BigInteger:%s) of %s too large for type int",
                    bigIntegerValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) value;
            if (bigDecimalValue.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) <= 0) {
                return bigDecimalValue.intValue();
            } else {
                throw new RuntimeException(String.format("column value(BigDecimal:%s) of %s too large for type int",
                    bigDecimalValue.toString(), fieldOrIndex));
            }
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not number",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public Date getDate(String field) {
        return getValue(field, null);
    }

    public Date getDate(String field, Date defaultValue) {
        return getValue(field, defaultValue);
    }

    public Date getDate(int index) {
        return getValue(index, null);
    }

    public Date getDate(int index, Date defaultValue) {
        return getValue(index, defaultValue);
    }

    public Float getFloat(String field) {
        return getFloat(field, get(field, null));
    }

    public Float getFloat(String field, float defaultValue) {
        return getFloat(field, get(field, defaultValue));
    }

    public Float getFloat(int index) {
        return getFloat(String.valueOf(index), get(index, null));
    }

    public Float getFloat(int index, float defaultValue) {
        return getFloat(String.valueOf(index), get(index, defaultValue));
    }

    private Float getFloat(String fieldOrIndex, Object value) {
        if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof Double) {
            Double doubleValue = (Double) value;
            if (doubleValue <= Float.MAX_VALUE) {
                return doubleValue.floatValue();
            } else {
                throw new RuntimeException(String.format("column value(Double:%s) of %s too large for type float",
                    doubleValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) value;
            if (bigDecimalValue.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) <= 0) {
                return bigDecimalValue.floatValue();
            } else {
                throw new RuntimeException(String.format("column value(BigDecimal:%s) of %s too large for type float",
                    bigDecimalValue.toString(), fieldOrIndex));
            }
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not float type",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public Double getDouble(String field) {
        return getDouble(field, get(field, null));
    }

    public Double getDouble(String field, Double defaultValue) {
        return getDouble(field, get(field, defaultValue));
    }

    public Double getDouble(int index) {
        return getDouble(String.valueOf(index), get(index, null));
    }

    public Double getDouble(int index, Double defaultValue) {
        return getDouble(String.valueOf(index), get(index, defaultValue));
    }

    private Double getDouble(String fieldOrIndex, Object value) {
        if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) value;
            if (bigDecimalValue.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) <= 0) {
                return bigDecimalValue.doubleValue();
            } else {
                throw new RuntimeException(String.format("column value(BigDecimal:%s) of %s too large for type double",
                    bigDecimalValue.toString(), fieldOrIndex));
            }
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not double type",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public Long getLong(String field) {
        return getLong(field, get(field, null));
    }

    public Long getLong(String field, long defaultValue) {
        return getLong(field, get(field, defaultValue));
    }

    public Long getLong(int index) {
        return getLong(String.valueOf(index), get(index, null));
    }

    public Long getLong(int index, long defaultValue) {
        return getLong(String.valueOf(index), get(index, defaultValue));
    }

    private Long getLong(String fieldOrIndex, Object value) {
        if (value instanceof Short) {
            return Long.valueOf((Short) value);
        } else if (value instanceof Integer) {
            return ((Integer)value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof BigInteger) {
            BigInteger bigIntegerValue = (BigInteger) value;
            if (bigIntegerValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
                return bigIntegerValue.longValue();
            } else {
                throw new RuntimeException(String.format("column value(BigInteger:%s) of %s too large for type long",
                    bigIntegerValue.toString(), fieldOrIndex));
            }
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) value;
            if (bigDecimalValue.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0) {
                return bigDecimalValue.longValue();
            } else {
                throw new RuntimeException(String.format("column value(BigDecimal:%s) of %s too large for type long",
                    bigDecimalValue.toString(), fieldOrIndex));
            }
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not number",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public BigInteger getBigInteger(String field) {
        return getBigInteger(field, get(field, null));
    }

    public BigInteger getBigInteger(String field, BigInteger defaultValue) {
        return getBigInteger(field, get(field, defaultValue));
    }

    public BigInteger getBigInteger(int index) {
        return getBigInteger(String.valueOf(index), get(index, null));
    }

    public BigInteger getBigInteger(int index, BigInteger defaultValue) {
        return getBigInteger(String.valueOf(index), get(index, defaultValue));
    }

    private BigInteger getBigInteger(String fieldOrIndex, Object value) {
        if (value instanceof Short) {
            return BigInteger.valueOf((Short) value);
        } else if (value instanceof Integer) {
            return BigInteger.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return BigInteger.valueOf((Long) value);
        } else if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toBigInteger();
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not number",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public BigDecimal getBigDecimal(String field) {
        return getBigDecimal(field, get(field, null));
    }

    public BigDecimal getBigDecimal(String field, BigDecimal defaultValue) {
        return getBigDecimal(field, get(field, defaultValue));
    }

    public BigDecimal getBigDecimal(int index) {
        return getBigDecimal(String.valueOf(index), get(index, null));
    }

    public BigDecimal getBigDecimal(int index, BigDecimal defaults) {
        return getBigDecimal(String.valueOf(index), get(index, defaults));
    }

    private BigDecimal getBigDecimal(String fieldOrIndex, Object value) {
        if (value instanceof Short) {
            return BigDecimal.valueOf((Short) value);
        } else if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else {
            throw new RuntimeException(String.format("type(%s) of column(%s) is not number",
                value.getClass().getName(), fieldOrIndex));
        }
    }

    public static Object get(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }

    public <T> T getValue(String field, T defaultValue) {
        Object value = get(field, defaultValue);

        return (T) value;
    }

    public <T> T getValue(int index, T defaultValue) {
        Object value = get(index, defaultValue);

        return (T) value;
    }

    /**
     * todo:
     *
     * rs.getArray();
     rs.getAsciiStream();
     rs.getBinaryStream();
     rs.getBlob();
     rs.getBytes();
     rs.getCharacterStream();
     rs.getClob();
     rs.getNCharacterStream();
     rs.getNClob();
     rs.getNString();
     rs.getRef();
     rs.getRowId();
     rs.getRow();
     rs.getSQLXML();
     rs.getType();
     rs.getURL();
     rs.getWarnings();
     */

    private boolean isInRange(String field) {
        int columnIndex = Optional.ofNullable(name2Index.get(field)).orElse(-1);
        return columnIndex >= 0 && columnIndex < values.length;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (schema == null) {
            str.append("{");
        } else {
            str.append(schema).append(": {");
        }

        for (int i = 0; i < this.values.length; i++) {
            String column = "";
            for (Entry<String, Integer> entry : name2Index.entrySet()) {
                if (i == entry.getValue().intValue()) {
                    column = entry.getKey();
                    break;
                }
            }

            str.append(column).append(": ").append(Builder.buildValue(values[i])).append(", ");
        }
        if (!name2Index.isEmpty()) {
            str.setLength(str.length() - ", ".length());
        }

        return str.toString();
    }
}
