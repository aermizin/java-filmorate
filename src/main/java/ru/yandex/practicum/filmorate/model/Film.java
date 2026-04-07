package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть null или пустым.")
    private String name;

    @NotBlank(message = "Описание фильма не может быть null или пустым.")
    private String description;

    @NotEmpty(message = "Фильм должен иметь хотя бы один жанр")
    private Set<Genre> genres = new HashSet<>();

    @NotBlank(message = "Рейтинг фильма не может быть null или пустым.")
    private Rating rating;

    @NotNull(message = "Дата выхода фильма не может быть null.")
    @PastOrPresent(message = "Дата выхода фильма не может быть в будущем.")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть null.")
    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private Integer duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();
}

