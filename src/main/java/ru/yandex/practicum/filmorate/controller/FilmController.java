package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

        @GetMapping
        public Collection<Film> findAll() {
            return filmService.findAll();
        }

        @PostMapping
        public Film createFilm(@Valid @RequestBody Film film) {
            return filmService.create(film);
        }

        @PutMapping
        public Film updateFilm(@Valid @RequestBody Film newFilm) {
            return filmService.update(newFilm);
        }
}