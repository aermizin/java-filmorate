package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreFilmDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_NUMBERS_OF_DESCRIPTIONS = 200;
    private static final int MIN_RATING_ID = 1;
    private static final int MAX_RATING_ID = 5;
    private static final int MAX_GENRE_ID = 6;

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        log.info("Получено общее количество {} фильмов ", films.size());
        return new LinkedHashSet<>(loadGenresToFilms(films));
    }

    public Film getFilmById(long filmId) {
        Optional<Film> filmOpt = filmStorage.findById(filmId);
        Film film = filmOpt.orElseThrow(() ->
                new FilmNotFoundException("Фильм с ID " + filmId + " не найден"));

        film.setGenres(filmStorage.findGenresByFilmId(film.getId()));
        log.info("Найден фильм с id = {} ", filmId);
        return film;
    }

    public Film create(Film film) {
        validateFilm(film);
        Film newFilm = filmStorage.create(film);
        Set<Genre> genres = filmStorage.findGenresByFilmId(newFilm.getId());

        newFilm.setGenres(genres);

        log.info("Создан новый фильм c id = {}", newFilm.getId());
        return newFilm;
    }

    public Film update(Film film) throws FilmNotFoundException {
        try {
            validateFilm(film);
            Film updateFilm = filmStorage.update(film);
            Set<Genre> genres = filmStorage.findGenresByFilmId(updateFilm.getId());

            updateFilm.setGenres(genres);

            log.info("Обновлены поля фильма c id = {}", updateFilm.getId());

            return updateFilm;
        } catch (NotFoundException e) {
            log.warn("Не удалось обновить фильм с id = {}", film.getId());
            throw new FilmNotFoundException(film.getId());
        }
    }

    public void addFilmLike(long filmId, long userId) {
        try {
            if (likeStorage.addLike(filmId, userId)) {
                log.info("Пользователь поставил лайк фильму: пользователь id ={}, фильм id ={}", filmId, userId);
                return;
            }

            log.warn("Пользователь с id = {} пытался повторно поставить лайк фильму с id = {}", userId, filmId);

        } catch (DataIntegrityViolationException e) {
            log.warn("Пользователь с id = {} или фильм с id ={} отсутствует", userId, filmId);
            throw new NotFoundException("Пользователь или фильма с таким id отсутствуют");
        }
    }

    public void deleteFilmLike(long filmId, long userId) {
        if (likeStorage.delete(filmId, userId)) {
            log.info("Пользователь c id ={} удалил лайк у фильма с id ={}.", userId, filmId);
            return;
        }
        log.warn("Лайк пользователя с id = {} фильму с id = {} не найден", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> popularFilms = filmStorage.getPopularFilmsByLikes(count);
        log.info("Успешно получено {} популярных фильмов", popularFilms.size());
        return new LinkedHashSet<>(loadGenresToFilms(popularFilms));
    }

    private Collection<Film> loadGenresToFilms(Collection<Film> films) {
        Collection<Long> filmsId = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Collection<GenreFilmDto> result = filmStorage.findGenresByFilmsId(filmsId);

        Map<Long, Set<Genre>> genresByFilmId = result.stream()
                .collect(Collectors.groupingBy(
                        GenreFilmDto::getFilmId,
                        Collectors.mapping(
                                dto -> Genre.builder()
                                        .id(dto.getId())
                                        .name(dto.getName())
                                        .build(),
                                Collectors.toSet()
                        )
                ));

        return films.stream()
                .map(film -> {
                    Set<Genre> genres = genresByFilmId.getOrDefault(film.getId(), Collections.emptySet());
                    film.setGenres(genres);
                    return film;
                })
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {

        Optional<Genre> invalidGenre = film.getGenres().stream()
                .filter(genre -> genre.getId() > MAX_GENRE_ID)
                .findFirst();

        if (invalidGenre.isPresent()) {
            Genre genre = invalidGenre.get();
            log.info("Недопустимый ID жанра у фильма: жанр ID={}, фильм ID={}",
                    genre.getId(), film.getId());
            throw new NotFoundException(
                    "Недопустимый ID жанра: " + genre.getId() + ". ID жанра не должен превышать " + MAX_GENRE_ID + "."
            );
        }

        if (film.getMpa().getId() > MAX_RATING_ID || film.getMpa().getId() < MIN_RATING_ID) {
            log.error("Допустимые значения id рейтинга от 1 до 5");
            throw new NotFoundException("Недопустимый ID рейтинга : " + film.getMpa().getId() +
                    ". Допустимые значения от " + MIN_RATING_ID + " до " + MAX_RATING_ID);
        }

        if (film.getDescription().length() > MAX_NUMBERS_OF_DESCRIPTIONS) {
            log.error("Ошибка валидации: описание  фильма не должно превышать 200 символов. Название фильма: {}",
                    film.getName());
            throw new ValidationException("Описание фильма не должно превышать 200 символов.");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Ошибка валидации: дата выпуска фильма должна быть не раньше 28 декабря 1895 года. Название фильма: {}",
                    film.getName());
            throw new ValidationException("Дата выпуска фильма должна быть не раньше 28 декабря 1895 года.");
        }
    }
}




