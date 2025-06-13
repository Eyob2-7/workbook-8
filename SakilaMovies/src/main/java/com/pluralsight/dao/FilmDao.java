package com.pluralsight.dao;

import com.pluralsight.models.Film;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmDao {
    private DataSource dataSource;

    public FilmDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Film> getFilmsByActorId(int actorId) throws SQLException {
        String sql = """
                SELECT f.film_id
                , f.title
                , f.description
                , f.release_year
                , f.length
                FROM film f
                JOIN film_actor fa ON f.film_id = fa.film_id
                WHERE fa.actor_id = ?
                ORDER BY f.title""";

        List<Film> films = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, actorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    films.add(new Film(
                            resultSet.getInt("film_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getString("release_year"),
                            resultSet.getInt("length")
                    ));
                }
            }
        }
        return films;
    }
}
