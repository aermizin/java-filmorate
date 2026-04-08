package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class LikeRepository extends AbstractRepository<Like> implements LikeStorage {
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String FIND_LIKE_BY_ID = "SELECT * FROM film_like WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_LIKE_BY_QUERY = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";


    private final RowMapper<Film> filmRowMapper;

    public LikeRepository(JdbcTemplate jdbc,
                          @Qualifier("likeRowMapper") RowMapper<Like> mapper,
                          RowMapper<Film> filmRowMapper) {
        super(jdbc, mapper);
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Optional<Like> findById(Long filmId, Long userId) {
        return findOne(FIND_LIKE_BY_ID, filmId, userId);
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        int rows = jdbc.update(INSERT_LIKE_QUERY, filmId, userId);
        return rows > 0;
    }

    @Override
    public boolean delete(Long filmId, Long userId) {
        return false;
    }
}
