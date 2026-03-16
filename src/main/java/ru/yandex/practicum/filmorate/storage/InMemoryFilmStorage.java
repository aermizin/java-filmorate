package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_NUMBERS_OF_DESCRIPTIONS = 200;


    private final Map<Long, Film> films = new HashMap<>();
    private long nextUserId = 1;

    @Override
    public Collection<Film> findAll() {
        int filmCount = films.size();
        log.info("Получено всех фильмов: {}", filmCount);
        return films.values();
    }

    @Override
    public Film create(Film newFilm) {
        log.info("Создание фильма: name = {}", newFilm.getName());
        validateFilm(newFilm);
        newFilm.setId(nextUserId++);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм успешно создан. Id: {}, name: {}", newFilm.getId(), newFilm.getName());
        return newFilm;
    }

    @Override
    public Film update(Film updateFilm) {
        if (updateFilm.getId() == null) {
            log.error("Ошибка валидации при обновлении фильма: ID не может быть null. Название фильма: {}",
                    updateFilm.getName());
            throw new ValidationException("Id фильма не может быть null");
        }

        if (!films.containsKey(updateFilm.getId())) {
            log.error("Ошибка валидации при обновлении фильма: ID фильма не найден. ID: {}", updateFilm.getId());
            throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден.");
        }

        Film newFilm = updateFilmFields(updateFilm);
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Поля фильма успешно обновлены. Id: {}", newFilm.getId());
        return newFilm;

    }

    @Override
    public Film getFilmById(Long filmId) {
        Film film = films.get(filmId);

        if (film == null) {
            String message = "Фильм с id=" + filmId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Успешно получен фильм с id={}", filmId);
        return film;
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

    private Film updateFilmFields(Film film) {
        Film updateFilm = films.get(film.getId());

        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());

        return updateFilm;
    }
}

