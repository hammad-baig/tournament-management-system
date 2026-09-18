package com.tournament.tournament_management_system.controller;

import com.tournament.tournament_management_system.dto.CreateParticipantRequest;
import com.tournament.tournament_management_system.dto.ParticipantResponse;
import com.tournament.tournament_management_system.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        ParticipantResponse response =
                participantService.createParticipant(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getParticipantById(
            @PathVariable Long id) {

        return participantService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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

        return participantService.updateParticipant(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(
            @PathVariable Long id) {

        if (participantService.deleteParticipant(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}