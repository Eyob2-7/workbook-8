package com.pluralsight.dao;

import com.pluralsight.models.Actor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorDao {
    private DataSource dataSource;

    public ActorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Actor> searchActorsByLastName(String lastName) throws SQLException {
        String sql = """
                SELECT actor_id
                , first_name
                , last_name
                 FROM actor
                 WHERE last_name = ?
                """;
        List<Actor> actors = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lastName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actors.add(new Actor(
                            rs.getInt("actor_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    ));
                }
            }
        }
        return actors;
    }
}