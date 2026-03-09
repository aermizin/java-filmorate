package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть null или пустым.")
    private String name;

    @NotBlank(message = "Описание фильма не может быть null или пустым.")
    private String description;

    @NotNull(message = "Дата выхода фильма не может быть null.")
    @PastOrPresent(message = "Дата выхода фильма не может быть в будущем.")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть null.")
    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private Integer duration;
}
