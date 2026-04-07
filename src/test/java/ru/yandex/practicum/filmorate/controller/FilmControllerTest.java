package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage.deleteTable();
    }

    private Film createTestFilm(
            Long filmId, String filmName, String filmDescription, LocalDate now, Integer filmDuration) {

        Film film = Film.builder()
                .id(filmId)
                .name(filmName)
                .description(filmDescription)
                .releaseDate(now)
                .duration(filmDuration)
                .mpa(new Rating(1L,"NC-17"))
                .genres(new LinkedHashSet<>())
                .build();

        return film;
    }

    @Test
    void shouldFindAllFilmsWhenFilmsExist() {
        // Given: создаём два фильма
        Film film1 = createTestFilm(1L, "Film One", "Description 1",
                LocalDate.of(2020, 1, 1),120);
        Film createdFilm1 = filmService.create(film1);

        Film film2 = createTestFilm(2L, "Film Two", "Description 2",
                LocalDate.of(2021, 1, 1), 90);
        Film createdFilm2 = filmService.create(film2);

        Collection<Film> films = filmService.findAll();

        assertThat(films).hasSize(2);
        assertThat(films)
                .extracting("name")
                .containsExactlyInAnyOrder("Film One", "Film Two");
    }

    @Test
    void shouldReturnEmptyCollectionWhenNoFilmsExist() {
        Collection<Film> films = filmService.findAll();

        assertThat(films).isEmpty();
    }

    @Test
    void shouldGetFilmByIdWhenFilmExists() {
        Film originalFilm = createTestFilm(1L, "Film One", "Description 1",
                LocalDate.of(2020, 1, 1),120);
        Film createdFilm = filmService.create(originalFilm);

        Film foundFilm = filmService.getFilmById(createdFilm.getId());

        assertThat(foundFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.getName()).isEqualTo("Film One");
        assertThat(foundFilm.getDescription()).isEqualTo("Description 1");
    }

    @Test
    void shouldThrowFilmNotFoundExceptionWhenFilmDoesNotExist() {
        assertThatThrownBy(() -> filmService.getFilmById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldUpdateFilmSuccessfully() {
        Film originalFilm = createTestFilm(1L, "Film One", "Description 1",
                LocalDate.of(2020, 1, 1),120);
        Film createdFilm = filmService.create(originalFilm);

        createdFilm.setName("Updated Name");
        createdFilm.setDescription("Updated Description");
        Film updatedFilm = filmService.update(createdFilm);

        Film fromDb = filmService.getFilmById(updatedFilm.getId());
        assertThat(fromDb.getName()).isEqualTo("Updated Name");
        assertThat(fromDb.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionTooLong() {
        // Given: фильм с описанием длиннее 200 символов
        String longDescription = "A".repeat(201); // 201 символ
        Film invalidFilm = createTestFilm(1L, "Film One", longDescription,
                LocalDate.of(2020, 1, 1),120);

        // When & Then: попытка создания должна вызвать исключение
        assertThatThrownBy(() -> filmService.create(invalidFilm))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("200 символов");
    }

    @Test
    void shouldThrowValidationExceptionWhenReleaseDateTooEarly() {
        Film invalidFilm = createTestFilm(1L, "Film One", "Description 1",
                LocalDate.of(1890, 1, 1),120);

        assertThatThrownBy(() -> filmService.create(invalidFilm))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("28 декабря 1895 года");
    }
}