package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Repository
public class FilmRepository extends AbstractRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name AS rating_name FROM film f LEFT JOIN rating r ON" +
            " f.mpa = r.id";
    private static final String FIND_FILM_BY_ID = "SELECT f.*, r.name AS rating_name FROM film f LEFT JOIN rating r ON" +
            " f.mpa = r.id WHERE f.id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO film (name, description, release_date, duration, mpa) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?," +
            " duration = ? WHERE id = ?";
    private static final String FIND_GENRE_ID_BY_ID = "SELECT g.id, g.name FROM genre AS g JOIN film_genre AS " +
            "fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id ASC";
    private static final String DELETE_TABLE_QUERY = "DELETE FROM film";

    private final RowMapper<Genre> genreRowMapper;

    public FilmRepository(JdbcTemplate jdbc,
                          @Qualifier("filmRowMapper") RowMapper<Film> mapper,
                          RowMapper<Genre> genreRowMapper) {
        super(jdbc, mapper);
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> findById(Long filmId) {
        return findOne(FIND_FILM_BY_ID, filmId);
    }

    @Override
    public Film create(Film newFilm) {
        java.sql.Date sqlReleaseDate = java.sql.Date.valueOf(newFilm.getReleaseDate());

        long id = insert(
                INSERT_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                sqlReleaseDate,
                newFilm.getDuration(),
                newFilm.getMpa().getId()
        );
        newFilm.setId(id);

        Set<Genre> genres = newFilm.getGenres();
        for (Genre genre : genres) {
            Long genreId = genre.getId();
            insertFilmGenreLink(id, genreId);
        }
        return findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с ID " + id + " не найден после создания"));
    }


    @Override
    public Film update(Film updateFilm) throws NotFoundException {
        java.sql.Date sqlReleaseDate = java.sql.Date.valueOf(updateFilm.getReleaseDate());

        update(
                UPDATE_FILM_QUERY,
                updateFilm.getName(),
                updateFilm.getDescription(),
                sqlReleaseDate,
                updateFilm.getDuration(),
                updateFilm.getId()
        );
        return updateFilm;
    }

    public Set<Genre> findGenresByFilmId(Long filmId) {
        return new LinkedHashSet<>(jdbc.query(FIND_GENRE_ID_BY_ID, genreRowMapper, filmId));
    }

    private void insertFilmGenreLink(Long filmId, Long genreId) {
        jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, genreId);
    }


    @Override
    public void deleteTable() {
        jdbc.execute(DELETE_TABLE_QUERY);
    }
}
