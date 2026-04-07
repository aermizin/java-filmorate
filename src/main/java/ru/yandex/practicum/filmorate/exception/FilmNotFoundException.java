package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends NotFoundException {
    public FilmNotFoundException(String message) {
        super(message);
    }
    public FilmNotFoundException(Long filmId) {
        super("Фильм с ID " + filmId + " не найден");
    }
}
