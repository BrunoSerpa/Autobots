package com.autobots.automanager.validar;

import java.util.List;
import java.util.Set;

public interface Validar<E> {
    List<String> verificar(E entity);
    List<String> verificar(Set<E> entities);
}