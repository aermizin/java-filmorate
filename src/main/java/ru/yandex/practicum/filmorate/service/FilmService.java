package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void addFilmLike(long filmId, long userId) {
        validateFilmAndUserExistence(filmId, userId);

        Film film = filmStorage.getFilmById(filmId);

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь с id={} ранее уже ставил like ", userId);
            throw new DuplicatedDataException("Пользователь с id = " + userId + " ранее уже ставил like фильму с name = "
                    + film.getName() + ".");
        }

        film.getLikes().add(userId);
        log.info("Пользователь с id={} поставил лайк фильму с id={}", userId, filmId);
    }

    public void deleteFilmLike(long filmId, long userId) {
        validateFilmAndUserExistence(filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id={} удалил лайк у фильма с id={}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.findAll());

        popularFilms.sort(Comparator.comparing((Film film) -> film.getLikes().size()).reversed());
        log.debug("Успешно отсортирован список рейтинговых фильмов состоящий из {} фильмов", popularFilms.size());
        return popularFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmAndUserExistence(long filmId, long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
    }
}




