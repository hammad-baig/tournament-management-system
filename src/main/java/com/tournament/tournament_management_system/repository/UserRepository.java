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

            System.out.println("🔥 Database URL: "
                    + connection.getMetaData().getURL());

            System.out.println("🔥 Database: "
                    + connection.getCatalog());

            System.out.println("🔥 Username parameter: ["
                    + username + "]");

            // Temporary diagnostic query
            try (
                    PreparedStatement debugStatement =
                            connection.prepareStatement(
                                    "SELECT username FROM public.users"
                            );
                    ResultSet debugResult =
                            debugStatement.executeQuery()
            ) {

                System.out.println("🔥 USERS SEEN BY SPRING:");

                while (debugResult.next()) {
                    System.out.println(
                            "   → [" + debugResult.getString("username") + "]"
                    );
                }
            }

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    System.out.println("✅ USER FOUND BY SQL QUERY");

                    User user = mapUser(resultSet);

                    return Optional.of(user);
                }

                System.out.println("❌ SQL QUERY RETURNED NO USER");

                return Optional.empty();
            }

        } catch (SQLException exception) {

            throw new UserRepositoryException(
                    "Failed to find user by username",
                    exception
            );
        }
    }

    public User createUser(User user) {

        String sql = """
            INSERT INTO users (
                username,
                email,
                password_hash,
                role
            )
            VALUES (?, ?, ?, ?)
            RETURNING id, username, email, password_hash, role, created_at
            """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }

                throw new UserRepositoryException(
                        "Failed to create user",
                        null
                );
            }

        } catch (SQLException exception) {
            throw new UserRepositoryException(
                    "Failed to create user",
                    exception
            );
        }
    }

    public boolean existsById(Long id) {

        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM users
                WHERE id = ?
            )
            """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }

                return false;
            }

        } catch (SQLException exception) {
            throw new UserRepositoryException(
                    "Failed to check whether user exists",
                    exception
            );
        }
    }

    public boolean existsByUsername(String username) {

        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM users
                WHERE username = ?
            )
            """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }

                return false;
            }

        } catch (SQLException exception) {
            throw new UserRepositoryException(
                    "Failed to check username",
                    exception
            );
        }
    }

    public boolean existsByEmail(String email) {

        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM users
                WHERE email = ?
            )
            """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }

                return false;
            }

        } catch (SQLException exception) {
            throw new UserRepositoryException(
                    "Failed to check email",
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