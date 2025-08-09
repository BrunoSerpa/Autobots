package com.autobots.automanager.converter;

import java.util.List;

public interface Converter<E, D> {
    E convertToEntity(D dto);

    D convertToDto(E entity);

    List<D> convertToDto(List<E> entities);
}