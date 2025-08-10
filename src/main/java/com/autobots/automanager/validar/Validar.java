package com.autobots.automanager.validar;

import java.util.List;

public interface Validar<E> {
    List<String> verificar(E entity);
}
