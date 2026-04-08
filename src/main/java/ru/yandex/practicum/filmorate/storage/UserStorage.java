package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

public interface UserStorage extends BaseStorage<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);
}

