package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("likeRowMapper")
public class LikeRowMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Like.builder()
                .id(resultSet.getLong("id"))
                .filmId(resultSet.getLong("film_id"))
                .userId(resultSet.getLong("user_id"))
                .build();
    }
}
