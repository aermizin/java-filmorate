package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public Collection<User> getUsers() {
        Collection<User> usersAll = userStorage.findAll();
        log.info("Успешно получено {} пользователей", usersAll.size());
        return usersAll;
    }

    public User getUserById(long userId) {
        Optional<User> userOpt = userStorage.findById(userId);

        return userOpt.orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new UserNotFoundException(userId);
        });
    }

    public User create(User user) {
        checkDataDuplication(user);
        User newUser = userStorage.create(user);
        log.info("Создан новый пользователь c id = {}", user.getId());
        return newUser;
    }

    public User update(User user) {
        try {
            User updateUser = userStorage.update(user);
            log.info("Обновлены поля пользователя c id = {}", updateUser.getId());
            return updateUser;
        } catch (NotFoundException e) {
            log.warn("Не удалось обновить фильм с id = {}", user.getId());
            throw new UserNotFoundException(user.getId());
        }
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }

        getUserById(friendId);

        Optional<Friendship> friendshipOpt = friendshipStorage.findById(userId, friendId);
        if (friendshipOpt.isPresent()) {
            throw new ValidationException("Пользователь уже добавил в друзья ");
        }

        Friendship newFriendship = Friendship.builder()
                .requesterId(userId)
                .friendId(friendId)
                .createdAt(LocalDateTime.now())
                .build();
        friendshipStorage.create(newFriendship);
    }

    public void deleteFriend(long userId, long friendId) {
        getUserById(friendId);
        getUserById(userId);

        Optional<Friendship> friendshipOpt = friendshipStorage.findById(userId, friendId);
        if (friendshipOpt.isEmpty()) {
            return;
        }
        friendshipStorage.delete(userId, friendId);
    }

    public Collection<User> findAllFriends(long userId) {
        getUserById(userId);
        log.info("Запрос на получение списка друзей пользователя с id={}", userId);
        Collection<User> allFriends = friendshipStorage.findAllFriends(userId);
        return allFriends;
    }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        log.info("Запрос на поиск общих друзей: userId={}, otherUserId={}", userId, otherUserId);

        Collection<User> friendsUser = friendshipStorage.findAllFriends(userId);
        Collection<User> friendsOtherUser = friendshipStorage.findAllFriends(otherUserId);

        return friendsUser.stream()
                .filter(friendsOtherUser::contains)
                .collect(Collectors.toSet());
    }

    private void checkDataDuplication(User user) {
        Optional<User> existingByEmail = userStorage.findByEmail(user.getEmail());
        if (existingByEmail.isPresent()) {
            log.warn("Данный имейл уже используется. Email: {}", user.getEmail());
            throw new DuplicatedDataException("данный имейл уже используется");
        }

        Optional<User> existingByLogin = userStorage.findByLogin(user.getLogin());
        if (existingByLogin.isPresent()) {
            log.warn("Данный логин уже используется. Login: {}", user.getLogin());
            throw new DuplicatedDataException("данный логин уже используется");
        }
    }



    private void createNewFriendshipRequest(long userId, long friendId) {
        Friendship newFriendship = Friendship.builder()
                .requesterId(userId)
                .friendId(friendId)
                .createdAt(LocalDateTime.now())
                .build();
        friendshipStorage.create(newFriendship);
    }
}

