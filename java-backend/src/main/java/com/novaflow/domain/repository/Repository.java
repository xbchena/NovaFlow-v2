package com.novaflow.domain.repository;

import com.novaflow.domain.model.shared.aggregate.AggregateRoot;

import java.util.List;
import java.util.Optional;

/**
 * 仓储基础接口
 * 定义所有仓储必须实现的基本操作
 */
public interface Repository<T extends AggregateRoot, ID> {

    /**
     * 保存聚合根
     */
    T save(T aggregate);

    /**
     * 根据ID查找聚合根
     */
    Optional<T> findById(ID id);

    /**
     * 查找所有聚合根
     */
    List<T> findAll();

    /**
     * 根据ID删除聚合根
     */
    void deleteById(ID id);

    /**
     * 检查聚合根是否存在
     */
    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}
