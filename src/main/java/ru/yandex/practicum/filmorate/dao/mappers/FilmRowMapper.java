package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Component
@Qualifier("filmRowMapper")
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        java.sql.Date sqlReleaseDate = resultSet.getDate("release_date");
        LocalDate releaseDate = sqlReleaseDate.toLocalDate();

        long ratingId = resultSet.getInt("mpa");
        String ratingName = resultSet.getString("rating_name");

        Film film = Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(releaseDate)
                .duration(resultSet.getInt("duration"))
                .mpa(new Rating(ratingId, ratingName))
                .genres(new LinkedHashSet<>())
                .build();

        return film;
    }

}
