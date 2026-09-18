package com.tournament.tournament_management_system.repository;

import com.tournament.tournament_management_system.model.Tournament;
import com.tournament.tournament_management_system.model.TournamentStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TournamentRepository {

    private final DataSource dataSource;

    public TournamentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Tournament createTournament(Tournament tournament) {

        String sql = """
                INSERT INTO tournaments (
                                                                      name,
                                                                      description,
                                                                      start_date,
                                                                      location,
                                                                      status,
                                                                      created_by
                                                                  )
                                                                  VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, tournament.getName());
            statement.setString(2, tournament.getDescription());
            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(tournament.getStartDate())
            );
            statement.setString(5, tournament.getLocation());
            statement.setString(6, tournament.getStatus().name());
            statement.setLong(7, tournament.getCreatedBy());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapTournament(resultSet);
                }

                throw new TournamentRepositoryException(
                        "Failed to create tournament",
                        null
                );
            }

        } catch (SQLException exception) {
            throw new TournamentRepositoryException(
                    "Failed to create tournament",
                    exception
            );
        }
    }

    public Optional<Tournament> findById(Long id) {

        String sql = """
                SELECT id, name, description, start_date, end_date,
                       location, status, created_by, created_at
                FROM tournaments
                WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapTournament(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new TournamentRepositoryException(
                    "Failed to find tournament by id",
                    exception
            );
        }
    }

    public List<Tournament> findAll() {

        String sql = """
                SELECT id, name, description, start_date, end_date,
                       location, status, created_by, created_at
                FROM tournaments
                ORDER BY id
                """;

        List<Tournament> tournaments = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                tournaments.add(mapTournament(resultSet));
            }

            return tournaments;

        } catch (SQLException exception) {
            throw new TournamentRepositoryException(
                    "Failed to find tournaments",
                    exception
            );
        }
    }

    public Optional<Tournament> updateTournament(
            Long id,
            Tournament tournament
    ) {

        String sql = """
                UPDATE tournaments
                SET name = ?,
                    description = ?,
                    start_date = ?,
                    end_date = ?,
                    location = ?,
                    status = ?
                WHERE id = ?
                RETURNING id, name, description, start_date, end_date,
                          location, status, created_by, created_at
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, tournament.getName());
            statement.setString(2, tournament.getDescription());
            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(tournament.getStartDate())
            );
            statement.setString(5, tournament.getLocation());
            statement.setString(6, tournament.getStatus().name());
            statement.setLong(7, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapTournament(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new TournamentRepositoryException(
                    "Failed to update tournament",
                    exception
            );
        }
    }

    public boolean deleteTournament(Long id) {

        String sql = """
                DELETE FROM tournaments
                WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new TournamentRepositoryException(
                    "Failed to delete tournament",
                    exception
            );
        }
    }

    private Tournament mapTournament(ResultSet resultSet)
            throws SQLException {

        Tournament tournament = new Tournament();

        tournament.setId(resultSet.getLong("id"));
        tournament.setName(resultSet.getString("name"));
        tournament.setDescription(resultSet.getString("description"));

        tournament.setStartDate(
                resultSet.getTimestamp("start_date").toLocalDateTime()
        );

        tournament.setLocation(resultSet.getString("location"));

        tournament.setStatus(
                TournamentStatus.valueOf(
                        resultSet.getString("status")
                )
        );

        tournament.setCreatedBy(
                resultSet.getLong("created_by")
        );

        tournament.setCreatedAt(
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );

        return tournament;
    }
}