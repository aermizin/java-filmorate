package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.Set;

public interface FilmStorage extends BaseStorage<Film> {
    Set<Genre> findGenresByFilmId(Long filmId);
}