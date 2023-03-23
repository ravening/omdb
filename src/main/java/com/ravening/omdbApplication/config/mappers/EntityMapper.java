package com.ravening.omdbApplication.config.mappers;

import java.util.Collection;

public interface EntityMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    Collection<D> toDto(Collection<E> entityList);

    Collection<E> toEntity(Collection<D> dtoList);
}
