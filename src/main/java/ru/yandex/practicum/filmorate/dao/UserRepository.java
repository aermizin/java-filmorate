package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class UserRepository extends AbstractRepository<User> implements UserStorage {

    private static final String FIND_ALL_USER_QUERY = "SELECT * FROM \"user\"";
    private static final String FIND_USER_BY_ID = "SELECT * FROM \"user\" WHERE id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM \"user\" WHERE email = ?";
    private static final String FIND_USER_BY_LOGIN = "SELECT * FROM \"user\" WHERE login = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO \"user\" (email, login, name, birthday)" +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE \"user\" SET email = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_TABLE_QUERY = "DELETE FROM \"user\"";

    public UserRepository(JdbcTemplate jdbcTemplate, @Qualifier("userRowMapper") RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_USER_QUERY);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return findOne(FIND_USER_BY_ID, userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_USER_BY_EMAIL, email);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return findOne(FIND_USER_BY_LOGIN, login);
    }

    public User create(User newUser) {
        java.sql.Date sqlBirthday = java.sql.Date.valueOf(newUser.getBirthday());

        long id = insert(
                INSERT_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                sqlBirthday
        );
        newUser.setId(id);
        return newUser;
    }

    public User update(User updateUser) throws NotFoundException {
        java.sql.Date sqlBirthday = java.sql.Date.valueOf(updateUser.getBirthday());

        update(
                UPDATE_USER_QUERY,
                updateUser.getEmail(),
                updateUser.getName(),
                sqlBirthday,
                updateUser.getId()
        );
        return updateUser;
    }
    public void deleteTable() {
        jdbc.execute(DELETE_TABLE_QUERY);
    }
}


