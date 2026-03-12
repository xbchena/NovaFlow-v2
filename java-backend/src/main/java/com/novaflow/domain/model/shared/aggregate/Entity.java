package com.novaflow.domain.model.shared.aggregate;

/**
 * 实体基类
 * 所有实体必须继承此类，实体具有唯一标识
 */
public abstract class Entity {

    /**
     * 获取实体ID
     */
    public abstract String getId();
}
