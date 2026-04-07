package ru.yandex.practicum.filmorate.storage;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
@Component
public interface UserStorage extends BaseStorage<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);
}

