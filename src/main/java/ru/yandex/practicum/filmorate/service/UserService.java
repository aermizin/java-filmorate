package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User newUser) {
        return userStorage.create(newUser);
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User update(User updateUser) {
        return userStorage.update(updateUser);
    }

    public void addFriend(long userId, long friendId) {
        validateUsersExistence(userId, friendId);

        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendId);

        if (user.getFriends().contains(friendId) || friendUser.getFriends().contains(userId)) {
            String message = "Дружба существует между двумя пользователями " + userId + " и " + friendId;
            log.warn(message);
            throw new DuplicatedDataException(message);
        }

        user.getFriends().add(friendId);
        friendUser.getFriends().add(userId);
        log.info("Друг с id={} успешно добавлен в список друзей пользователя id={}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        validateUsersExistence(userId, friendId);

        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(userId);
        log.info("Пользователь с id={} удалил дружбу с пользователем id={}", userId, friendId);
    }

    public Collection<User> findAllFriend(long userId) {
        User user = userStorage.getUserById(userId);

        if (user == null) {
            String message = "Пользователь с id = " + userId + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }

        Set<Long> friendsId = new HashSet<>(user.getFriends());

        if (friendsId.isEmpty()) {
            log.info("Список друзей пользователя с id={} пуст", userId);
            return Collections.emptySet();
        }

        return friendsId.stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        validateUsersExistence(userId, otherUserId);

        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);

        Set<Long> friendsOfUserId = new HashSet<>(user.getFriends());
        Set<Long> friendsOfOtherUserId = new HashSet<>(otherUser.getFriends());

        if (friendsOfUserId.isEmpty()) {
            log.info("У пользователя с id={} нет общих друзей с пользователем {}", userId, otherUserId);
            return Collections.emptySet();
        }

        return friendsOfUserId.stream()
                .filter(friendsOfOtherUserId::contains)
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateUsersExistence(long userId, long otherUserId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherUserId);
    }
}
