package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Optional;

public interface LikeStorage {
    Optional<Like> findById(Long filmId, Long userId);
    Collection<Film> getPopularFilmsByLikes (Integer count);
    boolean addLike(Long filmId, Long userId);
    boolean delete(Long filmId, Long userId);
}
