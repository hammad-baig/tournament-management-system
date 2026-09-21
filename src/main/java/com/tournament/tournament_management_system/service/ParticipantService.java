package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.CreateParticipantRequest;
import com.tournament.tournament_management_system.dto.ParticipantResponse;
import com.tournament.tournament_management_system.model.Participant;
import com.tournament.tournament_management_system.model.User;
import com.tournament.tournament_management_system.repository.ParticipantRepository;
import com.tournament.tournament_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final DataValidationService validationService;
    private final UserRepository userRepository;

    public ParticipantService(
            ParticipantRepository participantRepository,
            UserRepository userRepository,
            DataValidationService validationService) {

        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public ParticipantResponse createParticipantForUser(
            CreateParticipantRequest request,
            String username) {

        validationService.validateParticipant(request);

        var user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username " + username
                                        + " does not exist"
                        )
                );

        if (participantRepository.existsByUserId(user.getId())) {
            throw new ConflictException(
                    "User already has a participant profile"
            );
        }

        Participant participant = new Participant();

        participant.setUserId(user.getId());
        participant.setDisplayName(request.getDisplayName());
        participant.setRating(request.getRating());
        participant.setCountry(request.getCountry());

        Participant savedParticipant =
                participantRepository.createParticipant(participant);

        return toParticipantResponse(savedParticipant);
    }

    public Optional<ParticipantResponse> findById(Long id) {

        return participantRepository.findById(id)
                .map(this::toParticipantResponse);
    }

    public List<ParticipantResponse> findAll() {

        return participantRepository.findAll()
                .stream()
                .map(this::toParticipantResponse)
                .toList();
    }

    public Optional<ParticipantResponse> updateParticipant(
            Long id,
            CreateParticipantRequest request) {

        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Participant with id " + id + " does not exist"
            );
        }

        Participant participant = new Participant();

        participant.setDisplayName(request.getDisplayName());
        participant.setRating(request.getRating());
        participant.setCountry(request.getCountry());

        return participantRepository
                .updateParticipant(id, participant)
                .map(this::toParticipantResponse);
    }

    public boolean deleteParticipant(Long id) {

        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Participant with id " + id + " does not exist"
            );
        }
        return participantRepository.deleteParticipant(id);
    }

    /*
     * Checks whether a participant belongs to
     * the currently authenticated user.
     */
    public boolean isOwner(
            Long participantId,
            String username) {

        Participant participant =
                participantRepository.findById(participantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participant with id "
                                                + participantId
                                                + " does not exist"
                                )
                        );

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username "
                                        + username
                                        + " does not exist"
                        )
                );

        return participant.getUserId()
                .equals(user.getId());
    }

    private ParticipantResponse toParticipantResponse(
            Participant participant) {

        ParticipantResponse response = new ParticipantResponse();

        response.setId(participant.getId());
        response.setUserId(participant.getUserId());
        response.setDisplayName(participant.getDisplayName());
        response.setRating(participant.getRating());
        response.setCountry(participant.getCountry());
        response.setCreatedAt(participant.getCreatedAt());

        return response;
    }
}