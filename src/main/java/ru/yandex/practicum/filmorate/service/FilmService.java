package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    public Film getFilmById(long filmId) {
        Film film = filmStorage.getFilms().get(filmId);

        if (film == null) {
            String message = "Фильм с id=" + filmId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Успешно получен фильм с id={}", filmId);
        return film;
    }

    public void addFilmLike(long filmId, long userId) {
        validateFilmAndUserExistence(filmId, userId);

        Film film = filmStorage.getFilms().get(filmId);

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

        Film film = filmStorage.getFilms().get(filmId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id={} удалил лайк у фильма с id={}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.getFilms().values());

        popularFilms.sort(Comparator.comparing((Film film) -> film.getLikes().size()).reversed());
        log.debug("Успешно отсортирован список рейтинговых фильмов состоящий из {} фильмов", popularFilms.size());
        return popularFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmAndUserExistence(long filmId, long userId) {

        if (!filmStorage.getFilms().containsKey(filmId)) {
            String message = "Фильм с id=" + filmId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }

        if (!userStorage.getUsers().containsKey(userId)) {
            String message = "Пользователь с id=" + userId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }
}


