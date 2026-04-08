package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

public interface BaseStorage<T> {

    Collection<T> findAll();

    Optional<T> findById(Long id);

    T create(T entity);

    T update(T entity);

    void deleteTable();
}

