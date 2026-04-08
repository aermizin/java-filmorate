package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FriendshipRepository extends AbstractRepository<Friendship> implements FriendshipStorage {

    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO friendship (requester_id, friend_id, created_at) " +
            "VALUES (?, ?, ?)";
    private static final String FIND_FRIENDSHIP_QUERY = "SELECT * FROM friendship WHERE requester_id = ? AND friend_id = ?";
    private static final String DELETE_FRIENDSHIP_QUERY = "DELETE FROM friendship WHERE requester_id = ? AND friend_id = ?";
    private static final String FIND_ALL_FRIEND_QUERY = "SELECT u.* FROM \"user\" AS u JOIN friendship AS f ON" +
            " f.requester_id = ? AND f.friend_id = u.id ORDER BY f.created_at DESC";


    private final RowMapper<User> userRowMapper;

    public FriendshipRepository(JdbcTemplate jdbc,
                                @Qualifier("friendshipRowMapper") RowMapper<Friendship> mapper,
                                RowMapper<User> userRowMapper) {
        super(jdbc, mapper);
        this.userRowMapper = userRowMapper;
    }

    @Override
    public void create(Friendship friendship) {
        long id = insert(
                INSERT_FRIENDSHIP_QUERY,
                friendship.getRequesterId(),
                friendship.getFriendId(),
                friendship.getCreatedAt()
        );

        friendship.setId(id);
    }

    @Override
    public Optional<Friendship> findById(Long userId, Long friendId) {
        return findOne(FIND_FRIENDSHIP_QUERY, userId, friendId);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        delete(
                DELETE_FRIENDSHIP_QUERY,
                userId,
                friendId

        );
    }

    @Override
    public Collection<User> findAllFriends(Long userId) {
        return jdbc.query(
                FIND_ALL_FRIEND_QUERY,
                userRowMapper,
                userId
        );
    }
}
