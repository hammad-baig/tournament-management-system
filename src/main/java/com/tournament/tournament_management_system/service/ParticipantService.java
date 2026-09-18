package com.tournament.tournament_management_system.service;

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

    public ParticipantService(
            ParticipantRepository participantRepository,
            DataValidationService validationService) {

        this.participantRepository = participantRepository;
        this.validationService = validationService;
    }

    public ParticipantResponse createParticipant(
            CreateParticipantRequest request) {

        validationService.validateParticipant(request);
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

        Participant participant = new Participant();

        participant.setDisplayName(request.getDisplayName());
        participant.setRating(request.getRating());
        participant.setCountry(request.getCountry());

        return participantRepository
                .updateParticipant(id, participant)
                .map(this::toParticipantResponse);
    }

    public boolean deleteParticipant(Long id) {

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