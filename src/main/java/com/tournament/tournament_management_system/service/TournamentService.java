package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.CreateTournamentRequest;
import com.tournament.tournament_management_system.dto.TournamentResponse;
import com.tournament.tournament_management_system.model.Tournament;
import com.tournament.tournament_management_system.repository.TournamentRepository;
import com.tournament.tournament_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final DataValidationService validationService;
    private final UserRepository userRepository;

    public TournamentService(
            TournamentRepository tournamentRepository,
            UserRepository userRepository,
            DataValidationService validationService) {

        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public TournamentResponse createTournament(
            Long id,
            CreateTournamentRequest request) {

        if (!tournamentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Tournament with id " + id + " does not exist"
            );
        }

        validationService.validateTournament(request);

        if (!userRepository.existsById(request.getCreatedBy())) {
            throw new ResourceNotFoundException(
                    "User with id " + request.getCreatedBy()
                            + " does not exist"
            );
        }
        Tournament tournament = new Tournament();

        tournament.setName(request.getName());
        tournament.setDescription(request.getDescription());
        tournament.setStartDate(request.getStartDate());
        tournament.setLocation(request.getLocation());
        tournament.setStatus(request.getStatus());
        tournament.setCreatedBy(request.getCreatedBy());

        Tournament savedTournament =
                tournamentRepository.createTournament(tournament);

        return toTournamentResponse(savedTournament);
    }

    public Optional<TournamentResponse> findById(Long id) {

        return tournamentRepository.findById(id)
                .map(this::toTournamentResponse);
    }

    public List<TournamentResponse> findAll() {

        return tournamentRepository.findAll()
                .stream()
                .map(this::toTournamentResponse)
                .toList();
    }

    public Optional<TournamentResponse> updateTournament(
            Long id,
            CreateTournamentRequest request) {

        Tournament tournament = new Tournament();

        tournament.setName(request.getName());
        tournament.setDescription(request.getDescription());
        tournament.setStartDate(request.getStartDate());
        tournament.setLocation(request.getLocation());
        tournament.setStatus(request.getStatus());

        return tournamentRepository
                .updateTournament(id, tournament)
                .map(this::toTournamentResponse);
    }

    public boolean deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Tournament with id " + id + " does not exist"
            );
        }
        return tournamentRepository.deleteTournament(id);
    }

    private TournamentResponse toTournamentResponse(
            Tournament tournament) {

        TournamentResponse response = new TournamentResponse();

        response.setId(tournament.getId());
        response.setName(tournament.getName());
        response.setDescription(tournament.getDescription());
        response.setStartDate(tournament.getStartDate());
        response.setLocation(tournament.getLocation());
        response.setStatus(tournament.getStatus());
        response.setCreatedBy(tournament.getCreatedBy());
        response.setCreatedAt(tournament.getCreatedAt());

        return response;
    }
}