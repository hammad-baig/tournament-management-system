package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.RegisterParticipantRequest;
import com.tournament.tournament_management_system.dto.TournamentParticipantResponse;
import com.tournament.tournament_management_system.model.RegistrationStatus;
import com.tournament.tournament_management_system.model.TournamentParticipant;
import com.tournament.tournament_management_system.repository.ParticipantRepository;
import com.tournament.tournament_management_system.repository.TournamentParticipantRepository;
import com.tournament.tournament_management_system.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentParticipantService {

    private final TournamentParticipantRepository repository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final DataValidationService validationService;

    public TournamentParticipantService(
            TournamentParticipantRepository repository,
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            DataValidationService validationService) {

        this.repository = repository;
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.validationService = validationService;
    }

    public TournamentParticipantResponse registerParticipant(
            RegisterParticipantRequest request) {

        validationService.validateRegistration(request);

        if (!tournamentRepository.existsById(
                request.getTournamentId())) {

            throw new ResourceNotFoundException(
                    "Tournament with id "
                            + request.getTournamentId()
                            + " does not exist"
            );
        }

        if (!participantRepository.existsById(
                request.getParticipantId())) {

            throw new ResourceNotFoundException(
                    "Participant with id "
                            + request.getParticipantId()
                            + " does not exist"
            );
        }

        var existingRegistration =
                repository.findRegistration(
                        request.getTournamentId(),
                        request.getParticipantId()
                );

        if (existingRegistration.isPresent()) {

            TournamentParticipant registration =
                    existingRegistration.get();

            if (registration.getStatus()
                    == RegistrationStatus.REGISTERED) {

                throw new ConflictException(
                        "Participant is already registered"
                );
            }

            return repository.reactivateRegistration(
                            request.getTournamentId(),
                            request.getParticipantId()
                    )
                    .map(this::toResponse)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Registration no longer exists"
                            )
                    );
        }

        TournamentParticipant registration =
                new TournamentParticipant();

        registration.setTournamentId(
                request.getTournamentId()
        );

        registration.setParticipantId(
                request.getParticipantId()
        );

        registration.setStatus(
                RegistrationStatus.REGISTERED
        );

        return toResponse(
                repository.registerParticipant(registration)
        );
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

        if (tournamentId == null || tournamentId <= 0) {
            throw new ValidationException(
                    "Tournament ID must be greater than 0"
            );
        }

        if (participantId == null || participantId <= 0) {
            throw new ValidationException(
                    "Participant ID must be greater than 0"
            );
        }

        TournamentParticipant registration =
                repository.findRegistration(
                                tournamentId,
                                participantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participant is not registered in this tournament"
                                )
                        );

        if (registration.getStatus()
                == RegistrationStatus.WITHDRAWN) {

            throw new ConflictException(
                    "Participant has already withdrawn"
            );
        }

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

    private TournamentParticipant createRegistration(
            RegisterParticipantRequest request) {

        TournamentParticipant registration =
                new TournamentParticipant();

        registration.setTournamentId(request.getTournamentId());
        registration.setParticipantId(request.getParticipantId());
        registration.setStatus(RegistrationStatus.REGISTERED);

        return registration;
    }
}