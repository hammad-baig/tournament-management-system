package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.RegisterParticipantRequest;
import com.tournament.tournament_management_system.dto.TournamentParticipantResponse;
import com.tournament.tournament_management_system.model.RegistrationStatus;
import com.tournament.tournament_management_system.model.TournamentParticipant;
import com.tournament.tournament_management_system.repository.TournamentParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentParticipantService {

    private final TournamentParticipantRepository repository;
    private final DataValidationService validationService;

    public TournamentParticipantService(
            TournamentParticipantRepository repository,
            DataValidationService validationService) {

        this.repository = repository;
        this.validationService = validationService;
    }

    public TournamentParticipantResponse registerParticipant(
            RegisterParticipantRequest request) {

        validationService.validateRegistration(request);
        TournamentParticipant registration =
                new TournamentParticipant();

        registration.setTournamentId(request.getTournamentId());
        registration.setParticipantId(request.getParticipantId());
        registration.setStatus(RegistrationStatus.REGISTERED);

        TournamentParticipant savedRegistration =
                repository.registerParticipant(registration);

        return toResponse(savedRegistration);
    }

    public List<TournamentParticipantResponse> findParticipantsByTournament(
            Long tournamentId) {

        return repository.findParticipantsByTournamentId(tournamentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean withdrawParticipant(
            Long tournamentId,
            Long participantId) {

        return repository.withdrawParticipant(
                tournamentId,
                participantId
        );
    }

    private TournamentParticipantResponse toResponse(
            TournamentParticipant registration) {

        TournamentParticipantResponse response =
                new TournamentParticipantResponse();

        response.setTournamentId(
                registration.getTournamentId()
        );

        response.setParticipantId(
                registration.getParticipantId()
        );

        response.setRegisteredAt(
                registration.getRegisteredAt()
        );

        response.setStatus(
                registration.getStatus()
        );

        return response;
    }
}