package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreRepository extends AbstractRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_GENRE_QUERY = "SELECT * FROM genre";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genre WHERE id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_GENRE_QUERY);
    }

    @Override
    public Optional<Genre> findById(Long genreId) {
        return findOne(FIND_GENRE_BY_ID, genreId);
    }
}
