package com.tournament.tournament_management_system.controller;

import com.tournament.tournament_management_system.dto.RegisterParticipantRequest;
import com.tournament.tournament_management_system.dto.TournamentParticipantResponse;
import com.tournament.tournament_management_system.service.TournamentParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentParticipantController {

    private final TournamentParticipantService service;

    public TournamentParticipantController(
            TournamentParticipantService service) {

        this.service = service;
    }

    @PostMapping("/{tournamentId}/participants/{participantId}")
    public ResponseEntity<TournamentParticipantResponse>
    registerParticipant(
            @PathVariable Long tournamentId,
            @PathVariable Long participantId) {

        RegisterParticipantRequest request =
                new RegisterParticipantRequest();

        request.setTournamentId(tournamentId);
        request.setParticipantId(participantId);

        TournamentParticipantResponse response =
                service.registerParticipant(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{tournamentId}/participants")
    public ResponseEntity<List<TournamentParticipantResponse>>
    getTournamentParticipants(
            @PathVariable Long tournamentId) {

        return ResponseEntity.ok(
                service.findParticipantsByTournament(tournamentId)
        );
    }

    @DeleteMapping("/{tournamentId}/participants/{participantId}")
    public ResponseEntity<Void> withdrawParticipant(
            @PathVariable Long tournamentId,
            @PathVariable Long participantId) {

        boolean withdrawn =
                service.withdrawParticipant(
                        tournamentId,
                        participantId
                );

        if (withdrawn) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}