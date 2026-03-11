package com.novaflow.domain.model.shared.aggregate;

import lombok.EqualsAndHashCode;

/**
 * 实体基类
 * 所有实体必须继承此类，实体具有唯一标识
 */
@EqualsAndHashCode(of = "id")
public abstract class Entity {

    /**
     * 获取实体ID
     */
    public abstract String getId();
}
