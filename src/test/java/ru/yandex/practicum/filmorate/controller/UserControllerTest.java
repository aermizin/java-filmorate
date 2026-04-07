package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage.deleteTable();
    }

    private User createTestUser(Long userId, String userEmail, String userLogin, String userName, LocalDate now) {
        User user = User.builder()
                .id(userId)
                .email(userEmail)
                .login(userLogin)
                .name(userName)
                .birthday(now)
                .build();

        return user;
    }

    @Test
    public void testFindUserById() {

        User user1 = createTestUser(
                1L, "user1@test.com", "login1", "User One", LocalDate.now());

        User createdUser1 = userStorage.create(user1);

        Long userId = createdUser1.getId();

        Optional<User> userOptional = userStorage.findById(userId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userId)
                );
    }

    @Test
    void shouldReturnEmptyCollectionWhenNoUsersExist() {
        Collection<User> users = userStorage.findAll();

        assertThat(users).isEmpty();
    }

    @Test
    void shouldFindUserByIdWhenUserExists() {
        User user = createTestUser(
                1L, "find@test.com", "findLogin", "findUser", LocalDate.now());
        User createdUser = userStorage.create(user);

        Optional<User> userOpt = userStorage.findById(createdUser.getId());
        if (userOpt.isPresent()) {
            User foundUser = userOpt.get();

            assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
            assertThat(foundUser.getEmail()).isEqualTo("find@test.com");
        }
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldThrowDuplicatedDataExceptionWhenEmailAlreadyExists() {
        User existingUser = createTestUser(
                1L, "duplicate@test.com", "login1", "User One", LocalDate.now());
        userService.create(existingUser);

        User duplicateUser = createTestUser(2L, "duplicate@test.com", "login2",
                "User Two", LocalDate.now());

        assertThatThrownBy(() -> userService.create(duplicateUser))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("данный имейл уже используется");

        Collection<User> users = userService.getUsers();
        assertThat(users).hasSize(1);
    }

    @Test
    void shouldUpdateUserWithValidData() {
        User originalUser = createTestUser(
                1L, "find@test.com", "findLogin5", "findUser", LocalDate.now());
        User createdUser = userService.create(originalUser);

        createdUser.setName("Updated Name");
        createdUser.setEmail("Update Email");

        User updatedUser = userService.update(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("Update Email");
    }
}