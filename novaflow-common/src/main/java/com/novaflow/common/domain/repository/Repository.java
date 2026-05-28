package com.novaflow.common.domain.repository;

import com.novaflow.common.domain.aggregate.AggregateRoot;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends AggregateRoot, ID> {

    T save(T aggregate);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}
