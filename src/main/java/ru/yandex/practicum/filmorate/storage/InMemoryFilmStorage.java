package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
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

    private Film updateFilmFields(Film film) {
        Film updateFilm = films.get(film.getId());

        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());

        return updateFilm;
    }
}

