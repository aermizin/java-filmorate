package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.GenreFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.Collection;
import java.util.Set;

public interface FilmStorage extends BaseStorage<Film> {
    Collection<Film> getPopularFilmsByLikes(Integer count);

    Set<Genre> findGenresByFilmId(Long filmId);

    Collection<GenreFilmDto> findGenresByFilmsId(Collection<Long> filmsId);
}