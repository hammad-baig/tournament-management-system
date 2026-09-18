package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.CreateTournamentRequest;
import com.tournament.tournament_management_system.dto.TournamentResponse;
import com.tournament.tournament_management_system.model.Tournament;
import com.tournament.tournament_management_system.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final DataValidationService validationService;

    public TournamentService(
            TournamentRepository tournamentRepository,
            DataValidationService validationService) {

        this.tournamentRepository = tournamentRepository;
        this.validationService = validationService;
    }

    public TournamentResponse createTournament(
            CreateTournamentRequest request) {

        validationService.validateTournament(request);
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