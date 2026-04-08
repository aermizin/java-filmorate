package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RatingRepository extends AbstractRepository<Rating> implements RatingStorage {
    private static final String FIND_ALL_RATING_QUERY = "SELECT * FROM rating ORDER BY id ASC LIMIT 5";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM rating WHERE id = ?";

    public RatingRepository(JdbcTemplate jdbc, @Qualifier("ratingRowMapper") RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Rating> findAll() {
        return findMany(FIND_ALL_RATING_QUERY);
    }

    @Override
    public Optional<Rating> findById(Long ratingId) {
        return findOne(FIND_RATING_BY_ID, ratingId);
    }
}