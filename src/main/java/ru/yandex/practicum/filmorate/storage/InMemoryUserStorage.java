package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage{
    @Getter
    private final Map<Long, User> users = new HashMap<>();
    private long nextFilmId = 1;

    @Override
    public Collection<User> findAll() {
        log.info("Выполняется получение всех пользователей");
        return users.values();
    }

    @Override
    public User create(User newUser) {
        checkDataDuplication(newUser);

        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
            log.info("У пользователя с login = '{}' отсутствовало name, присвоено name = '{}'",
                    newUser.getLogin(), newUser.getName());
        }

        newUser.setId(nextFilmId++);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь успешно создан. Id: {}, login: '{}'", newUser.getId(), newUser.getLogin());
        return newUser;
    }

    @Override
    public User update(User updateUser) {
        if (updateUser.getId() == null) {
            log.error("Ошибка валидации при обновлении пользователя: id не может быть null. Логин пользователя: {}",
                    updateUser.getLogin());
            throw new ValidationException("Id пользователя не может быть null");
        }

        if (!users.containsKey(updateUser.getId())) {
            log.error("Ошибка валидации при обновлении фильма: ID фильма не найден. ID: {}", updateUser.getId());
            throw new NotFoundException("Фильм с id = " + updateUser.getId() + " не найден.");
        }

        //checkDataDuplication(updateUser);
        User newUser = updateUserFields(updateUser);
        log.info("Поля пользователя успешно обновлены. Id: {}", newUser.getId());
        users.put(newUser.getId(), newUser);
        return newUser;

    }

    private void checkDataDuplication(User profile) {
        boolean checkEmailDuplication = users.values()
                .stream()
                .anyMatch(user -> profile.getEmail().equals(user.getEmail()));

        if (checkEmailDuplication) {
            log.error("Ошибка валидации: этот email уже используется. Email: {}", profile.getEmail());
            throw new DuplicatedDataException("Этот email = " + profile.getEmail() + " уже используется.");
        }

        boolean checkLoginDuplication = users.values()
                .stream()
                .anyMatch(user -> profile.getLogin().equals(user.getLogin()));

        if (checkLoginDuplication) {
            log.error("Ошибка валидации: этот login уже используется. Login: {}", profile.getLogin());
            throw new DuplicatedDataException("Этот login = " + profile.getLogin() + " уже используется.");
        }
    }

    private User updateUserFields(User user) {
        User updateUser = users.get(user.getId());

        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        updateUser.setBirthday(user.getBirthday());

        return updateUser;
    }
}
