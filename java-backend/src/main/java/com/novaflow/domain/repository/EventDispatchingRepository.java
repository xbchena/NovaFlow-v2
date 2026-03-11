package com.novaflow.domain.repository;

import com.novaflow.domain.event.DomainEvent;
import com.novaflow.domain.model.shared.aggregate.AggregateRoot;

import java.util.Optional;

/**
 * 事件分发仓储接口
 * 扩展基础仓储接口，添加自动事件发布能力
 */
public interface EventDispatchingRepository<T extends AggregateRoot, ID> {

    /**
     * 保存聚合根并自动发布领域事件
     */
    T save(T aggregate);

    /**
     * 根据ID查找聚合根
     */
    Optional<T> findById(ID id);

    /**
     * 根据ID删除聚合根
     */
    void deleteById(ID id);
}
