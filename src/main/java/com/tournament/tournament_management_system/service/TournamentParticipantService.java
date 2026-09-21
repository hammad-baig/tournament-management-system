package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.RegisterParticipantRequest;
import com.tournament.tournament_management_system.dto.TournamentParticipantResponse;
import com.tournament.tournament_management_system.model.Participant;
import com.tournament.tournament_management_system.model.TournamentParticipant;
import com.tournament.tournament_management_system.model.RegistrationStatus;
import com.tournament.tournament_management_system.model.User;
import com.tournament.tournament_management_system.repository.ParticipantRepository;
import com.tournament.tournament_management_system.repository.TournamentParticipantRepository;
import com.tournament.tournament_management_system.repository.TournamentRepository;
import com.tournament.tournament_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentParticipantService {

    private final TournamentParticipantRepository repository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final DataValidationService validationService;

    public TournamentParticipantService(
            TournamentParticipantRepository repository,
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            UserRepository userRepository,
            DataValidationService validationService) {

        this.repository = repository;
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public TournamentParticipantResponse registerParticipant(
            RegisterParticipantRequest request,
            String username) {

        validationService.validateRegistration(request);

        if (!tournamentRepository.existsById(
                request.getTournamentId())) {

            throw new ResourceNotFoundException(
                    "Tournament with id "
                            + request.getTournamentId()
                            + " does not exist"
            );
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username "
                                        + username
                                        + " does not exist"
                        )
                );

        Participant participant =
                participantRepository
                        .findById(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Authenticated user does not have a participant profile"
                                )
                        );

        Long participantId = participant.getId();

        var existingRegistration =
                repository.findRegistration(
                        request.getTournamentId(),
                        participantId
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
                            participantId
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
                participantId
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
            String username) {

        if (tournamentId == null || tournamentId <= 0) {
            throw new ValidationException(
                    "Tournament ID must be greater than 0"
            );
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username "
                                        + username
                                        + " does not exist"
                        )
                );

        Participant participant =
                participantRepository
                        .findById(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Authenticated user does not have a participant profile"
                                )
                        );

        Long participantId = participant.getId();

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
}