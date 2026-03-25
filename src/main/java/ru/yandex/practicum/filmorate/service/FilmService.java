package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_NUMBERS_OF_DESCRIPTIONS = 200;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film update(Film newFilm) {
        validateFilm(newFilm);
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

    private void validateFilm(Film newFilm) {
        if (newFilm.getDescription().length() > MAX_NUMBERS_OF_DESCRIPTIONS) {
            log.error("Ошибка валидации: описание  фильма не должно превышать 200 символов. Название фильма: {}",
                    newFilm.getName());
            throw new ValidationException("Описание фильма не должно превышать 200 символов.");
        }

        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Ошибка валидации: дата выпуска фильма должна быть не раньше 28 декабря 1895 года. Название фильма: {}",
                    newFilm.getName());
            throw new ValidationException("Дата выпуска фильма должна быть не раньше 28 декабря 1895 года.");
        }
    }
}




