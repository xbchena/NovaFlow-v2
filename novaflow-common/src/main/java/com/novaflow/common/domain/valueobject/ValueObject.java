package com.novaflow.common.domain.valueobject;

public interface ValueObject {

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    static boolean equalOrFalse(ValueObject one, ValueObject two) {
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }
}
