package com.fishqq.biz.schemas;

import java.util.Map;
import java.util.Set;

import com.fishqq.orm.Schema;
import com.google.common.collect.Maps;
import org.reflections.Reflections;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/27
 */
public class Schemas {
    private static Map<Class<?>, Schema> InstanceMap = Maps.newHashMap();

    static {
        String fullClassName = Schemas.class.getName();
        String currPackageName = fullClassName.substring(0, fullClassName.lastIndexOf("."));

        Reflections reflections = new Reflections(currPackageName);

        Set<Class<? extends Schema>> schemas = reflections.getSubTypesOf(Schema.class);

        schemas.stream().forEach((Class<? extends Schema> type) -> {
            try {
                if (type.getName().startsWith(currPackageName)) {
                    InstanceMap.put(type, type.newInstance());
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("instance class " + type.getName() + " exception", e);
            }
        });
    }

    public static <T extends Schema> Schema get(Class<T> type) {
        return InstanceMap.get(type);
    }
}
