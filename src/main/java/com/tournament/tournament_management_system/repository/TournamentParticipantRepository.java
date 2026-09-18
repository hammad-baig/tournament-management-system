package com.tournament.tournament_management_system.repository;

import com.tournament.tournament_management_system.model.RegistrationStatus;
import com.tournament.tournament_management_system.model.TournamentParticipant;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TournamentParticipantRepository {

    private final DataSource dataSource;

    public TournamentParticipantRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TournamentParticipant registerParticipant(
            TournamentParticipant registration) {

        String sql = """
                INSERT INTO tournament_participants (
                    tournament_id,
                    participant_id,
                    status
                )
                VALUES (?, ?, ?)
                RETURNING tournament_id, participant_id,
                          registered_at, status
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, registration.getTournamentId());
            statement.setLong(2, registration.getParticipantId());
            statement.setString(3, registration.getStatus().name());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRegistration(resultSet);
                }

                throw new TournamentParticipantRepositoryException(
                        "Failed to register participant",
                        null
                );
            }

        } catch (SQLException exception) {
            throw new TournamentParticipantRepositoryException(
                    "Failed to register participant",
                    exception
            );
        }
    }

    public Optional<TournamentParticipant> findRegistration(
            Long tournamentId,
            Long participantId) {

        String sql = """
                SELECT tournament_id, participant_id,
                       registered_at, status
                FROM tournament_participants
                WHERE tournament_id = ?
                  AND participant_id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, tournamentId);
            statement.setLong(2, participantId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRegistration(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new TournamentParticipantRepositoryException(
                    "Failed to find tournament registration",
                    exception
            );
        }
    }

    public List<TournamentParticipant> findParticipantsByTournamentId(
            Long tournamentId) {

        String sql = """
                SELECT tournament_id, participant_id,
                       registered_at, status
                FROM tournament_participants
                WHERE tournament_id = ?
                  AND status = 'REGISTERED'
                ORDER BY registered_at
                """;

        List<TournamentParticipant> registrations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, tournamentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    registrations.add(mapRegistration(resultSet));
                }

                return registrations;
            }

        } catch (SQLException exception) {
            throw new TournamentParticipantRepositoryException(
                    "Failed to find tournament participants",
                    exception
            );
        }
    }

    public boolean withdrawParticipant(
            Long tournamentId,
            Long participantId) {

        String sql = """
                UPDATE tournament_participants
                SET status = 'WITHDRAWN'
                WHERE tournament_id = ?
                  AND participant_id = ?
                  AND status = 'REGISTERED'
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, tournamentId);
            statement.setLong(2, participantId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new TournamentParticipantRepositoryException(
                    "Failed to withdraw participant",
                    exception
            );
        }
    }

    public Optional<TournamentParticipant> reactivateRegistration(
            Long tournamentId,
            Long participantId) {

        String sql = """
            UPDATE tournament_participants
            SET status = 'REGISTERED'
            WHERE tournament_id = ?
              AND participant_id = ?
            RETURNING tournament_id, participant_id,
                      registered_at, status
            """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, tournamentId);
            statement.setLong(2, participantId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRegistration(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new TournamentParticipantRepositoryException(
                    "Failed to reactivate registration",
                    exception
            );
        }
    }

    private TournamentParticipant mapRegistration(
            ResultSet resultSet) throws SQLException {

        TournamentParticipant registration =
                new TournamentParticipant();

        registration.setTournamentId(
                resultSet.getLong("tournament_id")
        );

        registration.setParticipantId(
                resultSet.getLong("participant_id")
        );

        registration.setRegisteredAt(
                resultSet.getTimestamp("registered_at")
                        .toLocalDateTime()
        );

        registration.setStatus(
                RegistrationStatus.valueOf(
                        resultSet.getString("status")
                )
        );

        return registration;
    }
}