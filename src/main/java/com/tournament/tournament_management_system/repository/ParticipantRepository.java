package com.tournament.tournament_management_system.repository;

import com.tournament.tournament_management_system.model.Participant;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ParticipantRepository {

    private final DataSource dataSource;

    public ParticipantRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Participant createParticipant(Participant participant) {

        String sql = """
                INSERT INTO participants (
                    user_id,
                    display_name,
                    rating,
                    country
                )
                VALUES (?, ?, ?, ?)
                RETURNING id, user_id, display_name, rating,
                          country, created_at
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, participant.getUserId());
            statement.setString(2, participant.getDisplayName());
            statement.setInt(3, participant.getRating());
            statement.setString(4, participant.getCountry());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapParticipant(resultSet);
                }

                throw new ParticipantRepositoryException(
                        "Failed to create participant",
                        null
                );
            }

        } catch (SQLException exception) {
            throw new ParticipantRepositoryException(
                    "Failed to create participant",
                    exception
            );
        }
    }

    public Optional<Participant> findById(Long id) {

        String sql = """
                SELECT id, user_id, display_name, rating,
                       country, created_at
                FROM participants
                WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapParticipant(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new ParticipantRepositoryException(
                    "Failed to find participant by id",
                    exception
            );
        }
    }

    public List<Participant> findAll() {

        String sql = """
                SELECT id, user_id, display_name, rating,
                       country, created_at
                FROM participants
                ORDER BY id
                """;

        List<Participant> participants = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                participants.add(mapParticipant(resultSet));
            }

            return participants;

        } catch (SQLException exception) {
            throw new ParticipantRepositoryException(
                    "Failed to find participants",
                    exception
            );
        }
    }

    public Optional<Participant> updateParticipant(
            Long id,
            Participant participant
    ) {

        String sql = """
                UPDATE participants
                SET display_name = ?,
                    rating = ?,
                    country = ?
                WHERE id = ?
                RETURNING id, user_id, display_name, rating,
                          country, created_at
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, participant.getDisplayName());
            statement.setInt(2, participant.getRating());
            statement.setString(3, participant.getCountry());
            statement.setLong(4, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapParticipant(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new ParticipantRepositoryException(
                    "Failed to update participant",
                    exception
            );
        }
    }

    public boolean deleteParticipant(Long id) {

        String sql = """
                DELETE FROM participants
                WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new ParticipantRepositoryException(
                    "Failed to delete participant",
                    exception
            );
        }
    }

    private Participant mapParticipant(ResultSet resultSet)
            throws SQLException {

        Participant participant = new Participant();

        participant.setId(resultSet.getLong("id"));
        participant.setUserId(resultSet.getLong("user_id"));
        participant.setDisplayName(
                resultSet.getString("display_name")
        );
        participant.setRating(
                resultSet.getInt("rating")
        );
        participant.setCountry(
                resultSet.getString("country")
        );
        participant.setCreatedAt(
                resultSet.getTimestamp("created_at")
                        .toLocalDateTime()
        );

        return participant;
    }
}