package org.xubin.game.attribute;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 属性工具类
 */

@Slf4j
public class AttrUtils {
    public static BaseAttr attrMap2BaseAttr(Map<String, Long> attrMap) {
        BaseAttr baseAttr = new BaseAttr();
        Class<?> clazz = baseAttr.getClass();
        for (Map.Entry<String, Long> entry : attrMap.entrySet()) {
            String attrName = entry.getKey();
            try {
                Field field = clazz.getDeclaredField(attrName);
                if (isAttrField(field)) {
                    field.setAccessible(true);
                    long attrValue = entry.getValue();
                    long oldValue = field.getLong(baseAttr);
                    field.set(baseAttr, oldValue + attrValue);
                }
            } catch (Exception e) {
                log.error("handle attr {} error", attrName, e);
            }
        }
        return baseAttr;
    }

    public static BaseAttr mergeBaseAttr(BaseAttr baseAttr1, BaseAttr baseAttr2) {
        BaseAttr result = new BaseAttr();
        Class<?> clazz = result.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == long.class) {
                    long value1 = field.getLong(baseAttr1);
                    long value2 = field.getLong(baseAttr2);
                    field.setLong(result, value1 + value2);
                } else if(field.getType() == int.class) {
                    int value1 = field.getInt(baseAttr1);
                    int value2 = field.getInt(baseAttr2);
                    field.setInt(result, value1 + value2);
                } else if (field.getType() == double.class) {
                    double value1 = field.getDouble(baseAttr1);
                    double value2 = field.getDouble(baseAttr2);
                    field.setDouble(result, value1 + value2);
                }
            }
        } catch (Exception e) {
            log.error("mergeBaseAttr error", e);
        }
        return result;
    }

    public static List<AttrChange> calcAttrChange(BaseAttr oldBaseAttr, BaseAttr newBaseAttr) {
        log.info("oldBaseAttr:{}, newBaseAttr:{}", oldBaseAttr, newBaseAttr);
        List<AttrChange> diff = new ArrayList<>();
        Class<?> clazz = oldBaseAttr.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (isAttrField(field)) {
                    long oldValue = field.getLong(oldBaseAttr);
                    long newValue = field.getLong(newBaseAttr);
                    if (oldValue != newValue) {
                        AttrChange attrChange = new AttrChange(field.getName(), newValue - oldValue, newValue);
                        diff.add(attrChange);
                    }
                }
            }
        } catch (Exception e) {
            log.error("diffBaseAttr error", e);
        }
        return diff;
    }

    public static boolean isAttrField(Field field) {
        Class<?> type = field.getType();
        return type == long.class || type == int.class || type == double.class;
    }
}
