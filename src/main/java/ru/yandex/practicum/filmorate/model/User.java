package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;

    @Email(message = "Email пользователя должен быть заполнен корректно.")
    @NotBlank(message = "Email пользователя не может быть null или пустым.")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть null или пустым.")
    private String login;

    private String name;

    @PastOrPresent(message = "День рождения пользователя не может быть в будущем.")
    @NotNull(message = "День рождения пользователя не может быть null.")
    private LocalDate birthday;

    @Builder.Default
    private Set<Friendship> friendship = new HashSet<>();
}
