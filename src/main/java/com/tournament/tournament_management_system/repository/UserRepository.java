package com.tournament.tournament_management_system.repository;

import com.tournament.tournament_management_system.model.UserRole;
import org.springframework.stereotype.Repository;
import com.tournament.tournament_management_system.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

@Repository
public class UserRepository {

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByUsername(String username) {

        String sql = """
            SELECT id, username, email, password_hash, role, created_at
            FROM public.users
            WHERE username = ?
            """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    User user = mapUser(resultSet);
                    return Optional.of(user);
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new UserRepositoryException(
                    "Failed to find user by username",
                    exception
            );
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {

        User user = new User();

        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(UserRole.valueOf(resultSet.getString("role")));
        user.setCreatedAt(
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );

        return user;
    }

}