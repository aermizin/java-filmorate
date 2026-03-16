package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void shouldPassValidationWhenUserFieldsAreAtBoundaryValues() {
        User validUser = User.builder()
                .email("user@test.com")
                .login("default-login")
                .name(null)
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        User user = userController.createUser(validUser);

        assertTrue(violations.isEmpty(), "Не обнаружено нарушений валидации.");
        assertNotNull(user.getId());
        assertEquals(user.getEmail(), validUser.getEmail());
        assertEquals(user.getLogin(), validUser.getLogin());
        assertEquals(user.getName(), validUser.getName());
        assertEquals(user.getBirthday(), validUser.getBirthday());
        assertTrue(userController.findAll().contains(validUser), "Добавленный пользователь присутствует " +
                "в хранилище.");
    }

    @Test
    void shouldFailValidationWhenUserEmailIsInvalid() {
        User validUser = User.builder()
                .email("user%test.com")
                .login("default-login")
                .name("default-name")
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Email пользователя должен быть заполнен корректно.", message);
    }

    @Test
    void shouldFailValidationWhenFilmEmailIsEmpty() {
        User validUser = User.builder()
                .email("")
                .login("default-login")
                .name("default-name")
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Email пользователя не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmEmailIsNull() {
        User validUser = User.builder()
                .email(null)
                .login("default-login")
                .name("default-name")
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Email пользователя не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmLoginIsEmpty() {
        User validUser = User.builder()
                .email("user@test.com")
                .login("")
                .name("default-name")
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Логин пользователя не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenFilmLoginIsNull() {
        User validUser = User.builder()
                .email("user@test.com")
                .login(null)
                .name("default-name")
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("Логин пользователя не может быть null или пустым.", message);
    }

    @Test
    void shouldFailValidationWhenUserBirthDateIsInFuture() {
        User validUser = User.builder()
                .email("user@test.com")
                .login("default-login")
                .name("default-name")
                .birthday(LocalDate.of(3000, 5,7))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("День рождения пользователя не может быть в будущем.", message);
    }

    @Test
    void shouldFailValidationWhenFilmBirthdayIsNull() {
        User validUser = User.builder()
                .email("user@test.com")
                .login("default-login")
                .name("default-name")
                .birthday(null)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(1, violations.size(), "Обнаружено 1 нарушение валидации");
        assertEquals("День рождения пользователя не может быть null.", message);
    }
}
