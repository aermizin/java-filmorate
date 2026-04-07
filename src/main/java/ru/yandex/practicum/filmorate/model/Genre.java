package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.ValidationException;

public enum Genre {
    COMEDY, DRAMA, ANIMATION, THRILLER, DOCUMENTARY, ACTION;

    public static Genre from(String genre) {
        switch(genre.toLowerCase().trim()) {
            case "комедия":
                return COMEDY;
            case "драма":
                return DRAMA;
            case "мультфильм":
                return ANIMATION;
            case "триллер":
                return THRILLER;
            case "документальный":
                return DOCUMENTARY;
            case "боевик":
                return ACTION;
            default:
                throw new ValidationException("Неизвестный жанр: " + genre);
        }
    }
}
