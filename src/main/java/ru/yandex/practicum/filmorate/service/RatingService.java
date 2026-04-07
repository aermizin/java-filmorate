package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<Rating> getRating() {
        return ratingStorage.findAll();
    }

    public Rating getRatingById(Long id) {
        Optional<Rating> ratingOpt = ratingStorage.findById(id);

        return ratingOpt.orElseThrow(() -> {
            String message = "Рейтинг с ID " + id + " не найден";
            log.warn(message);
            return new NotFoundException(message);
        });
    }
}
