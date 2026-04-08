package ru.yandex.practicum.filmorate.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreFilmDto {
    private Long id;

    private String name;

    private Long filmId;
}

