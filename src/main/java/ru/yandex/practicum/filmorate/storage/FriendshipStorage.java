package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FriendshipStorage {
    void create(Friendship friendship);
    Optional<Friendship> findById(Long userId, Long friendId);
    void delete(Long userId, Long friendId);
    Collection<User> findAllFriends(Long userId);
}
