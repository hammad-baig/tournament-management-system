package com.tournament.tournament_management_system.controller;

import com.tournament.tournament_management_system.dto.CreateParticipantRequest;
import com.tournament.tournament_management_system.dto.ParticipantResponse;
import com.tournament.tournament_management_system.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }


    @PostMapping
    public ResponseEntity<ParticipantResponse> createParticipant(
            @RequestBody CreateParticipantRequest request) {

        String username = getAuthenticatedUsername();

        ParticipantResponse response =
                participantService.createParticipantForUser(
                        request,
                        username
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getParticipantById(
            @PathVariable Long id) {

        String username = getAuthenticatedUsername();

        if (!participantService.isOwner(id, username)) {
            throw new AccessDeniedException(
                    "You can only access your own participant profile"
            );
        }

        Optional<ParticipantResponse> participant =
                participantService.findById(id);

        if (participant.isPresent()) {
            return ResponseEntity.ok(participant.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ParticipantResponse>> getAllParticipants() {

        return ResponseEntity.ok(
                participantService.findAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipantResponse> updateParticipant(
            @PathVariable Long id,
            @RequestBody CreateParticipantRequest request) {

        String username = getAuthenticatedUsername();

        if (!participantService.isOwner(id, username)) {
            throw new AccessDeniedException(
                    "You can only update your own participant profile"
            );
        }

        return participantService.updateParticipant(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(
            @PathVariable Long id) {

        String username = getAuthenticatedUsername();

        if (!participantService.isOwner(id, username)) {
            throw new AccessDeniedException(
                    "You can only delete your own participant profile"
            );
        }

        if (participantService.deleteParticipant(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private String getAuthenticatedUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }
}