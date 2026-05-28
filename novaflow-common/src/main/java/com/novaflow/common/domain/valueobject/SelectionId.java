package com.novaflow.common.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class SelectionId {

    private final String value;

    private SelectionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("选择ID不能为空");
        }
        this.value = value;
    }

    public static SelectionId of(String value) {
        return new SelectionId(value);
    }

    public static SelectionId generate() {
        return new SelectionId(UUID.randomUUID().toString().replace("-", ""));
    }

    public static SelectionId fromUUID(UUID uuid) {
        return new SelectionId(uuid.toString().replace("-", ""));
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}
