package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDateTime;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        java.sql.Timestamp sqlCreatedAt = resultSet.getTimestamp("created_at");
        LocalDateTime createdAt = sqlCreatedAt.toLocalDateTime();

        return Friendship.builder()
                .id(resultSet.getLong("id"))
                .requesterId(resultSet.getLong("requester_id"))
                .friendId(resultSet.getLong("friend_id"))
                .createdAt(createdAt)
                .build();
    }
}
