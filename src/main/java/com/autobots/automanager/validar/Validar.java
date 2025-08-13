package com.autobots.automanager.validar;

import java.util.List;

public interface Validar<E> {
    List<String> verificar(E entity);
    List<String> verificar(List<E> entities);
}