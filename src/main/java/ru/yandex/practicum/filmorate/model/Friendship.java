package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Friendship {
    private Long id;

    @NotNull(message = "Инициатор дружбы не может быть null.")
    private User requester;

    @NotNull(message = "Пользователь не может быть null.")
    private User friend;

    @NotNull(message = "Статус дружбы обязателен.")
    private FriendshipStatus status;

    @NotNull(message = "Дата создания запроса не может быть null.")
    private LocalDateTime createdAt;
}
