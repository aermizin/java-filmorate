package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private FilmService filmService;
    private UserService userService;
    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void shouldPassValidationWhenFilmFieldsAreAtBoundaryValues() {
        Film validFilm = Film.builder()
                .name("default-name")
                .description("a".repeat(200))
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        Film film = filmController.createFilm(validFilm);

        assertTrue(violations.isEmpty(), "Не обнаружено нарушений валидации.");
        assertNotNull(film.getId());
        assertEquals(film.getName(), validFilm.getName());
        assertEquals(film.getDescription(), validFilm.getDescription());
        assertEquals(film.getReleaseDate(), validFilm.getReleaseDate());
        assertEquals(film.getDuration(), validFilm.getDuration());
        assertTrue(filmController.findAll().contains(validFilm), "Добавленный фильм присутствует в хранилище.");
    }

    @Test
    void shouldFailValidationWhenFilmNameIsEmpty() {
        Film invalidFilm = Film.builder()
                .name("")
                .description("default-description")
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Название фильма не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmNameIsNull() {
        Film invalidFilm = Film.builder()
                .name(null)
                .description("default-description")
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Название фильма не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmDescriptionIsEmpty() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description("")
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Описание фильма не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmDescriptionIsNull() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description(null)
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Описание фильма не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmReleaseDateIsNull() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description("default-description")
                .releaseDate(null)
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Дата выхода фильма не может быть null.", message);
    }

    @Test
    void shouldFailValidationWhenFilmReleaseDateIsInFuture() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description("default-description")
                .releaseDate(LocalDate.of(3000, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Дата выхода фильма не может быть в будущем.", message);
    }

    @Test
    void shouldFailValidationWhenFilmDurationIsNull() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description("default-description")
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(null)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Продолжительность фильма не может быть null.", message);
    }

    @Test
    void shouldFailValidationWhenFilmDurationIsZero() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description("default-description")
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Продолжительность фильма должна быть положительным числом.", message);
    }

    @Test
    void shouldFailValidationWhenFilmDurationIsNegative() {
        Film invalidFilm = Film.builder()
                .name("default-name")
                .description("default-description")
                .releaseDate(LocalDate.of(2000, 12, 28))
                .duration(-1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Продолжительность фильма должна быть положительным числом.", message);
    }
}