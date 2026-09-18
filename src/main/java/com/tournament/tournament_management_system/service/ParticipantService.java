package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.repository.UserRepository;
import com.tournament.tournament_management_system.service.ConflictException;
import com.tournament.tournament_management_system.service.ResourceNotFoundException;
import com.tournament.tournament_management_system.dto.CreateParticipantRequest;
import com.tournament.tournament_management_system.dto.ParticipantResponse;
import com.tournament.tournament_management_system.model.Participant;
import com.tournament.tournament_management_system.repository.ParticipantRepository;
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

    public ParticipantResponse createParticipant(
            CreateParticipantRequest request) {

        validationService.validateParticipant(request);

        if (!userRepository.existsById(request.getUserId())) {
            throw new ResourceNotFoundException(
                    "User with id " + request.getUserId()
                            + " does not exist"
            );
        }

        if (participantRepository.existsByUserId(request.getUserId())) {
            throw new ConflictException(
                    "User already has a participant profile"
            );
        }
        Participant participant = new Participant();

        participant.setUserId(request.getUserId());
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