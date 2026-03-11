package com.novaflow.domain.model.shared.valueobject;

import java.util.Objects;

/**
 * 值对象接口
 * 所有值对象必须实现此接口
 * 值对象通过其属性值来标识相等性，而不是ID
 */
public interface ValueObject {

    /**
     * 值对象相等性比较
     * 实现类应该基于所有字段进行比较
     */
    @Override
    boolean equals(Object obj);

    /**
     * 值对象哈希码
     * 实现类应该基于所有字段计算哈希码
     */
    @Override
    int hashCode();

    /**
     * 默认的值对象相等性实现
     * 基于所有字段的相等性比较
     */
    static boolean equalOrFalse(ValueObject one, ValueObject two) {
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }
}
