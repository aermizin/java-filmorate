package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Friendship {
    private Long id;

    @NotNull(message = "Инициатор дружбы не может быть null.")
    private Long requesterId;

    @NotNull(message = "Пользователь не может быть null.")
    private Long friendId;

    @NotNull(message = "Дата создания запроса не может быть null.")
    private LocalDateTime createdAt;
}
