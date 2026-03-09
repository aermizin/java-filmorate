package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @Email(message = "Email пользователя должен быть заполнен корректно.")
    @NotBlank(message = "Email пользователя не может быть null или пустым.")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть null или пустым.")
    private String login;

    private String name;

    @PastOrPresent(message = "День рождение пользователя не может быть в будущем.")
    @NotNull(message = "День рождение пользователя не может быть null.")
    private LocalDate birthday;
}
