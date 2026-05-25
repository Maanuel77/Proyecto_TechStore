package com.salesianos.triana.techstore.service.base;

import java.util.List;
import java.util.Optional;

// Operaciones CRUD comunes a todos los servicios. Cada servicio concreto
// extiende BaseServiceImpl y solo añade su lógica específica.
public interface BaseService<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T t);

    T edit(T t);

    void delete(T t);

    void deleteById(ID id);
}
