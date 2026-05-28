package com.novaflow.common.domain.repository;

import com.novaflow.common.domain.aggregate.AggregateRoot;

import java.util.Optional;

public interface EventDispatchingRepository<T extends AggregateRoot, ID> {

    T save(T aggregate);

    Optional<T> findById(ID id);

    void deleteById(ID id);
}
