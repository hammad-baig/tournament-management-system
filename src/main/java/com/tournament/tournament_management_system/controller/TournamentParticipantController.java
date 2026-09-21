package com.tournament.tournament_management_system.controller;

import com.tournament.tournament_management_system.dto.RegisterParticipantRequest;
import com.tournament.tournament_management_system.dto.TournamentParticipantResponse;
import com.tournament.tournament_management_system.service.TournamentParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/{tournamentId}/participants")
    public ResponseEntity<TournamentParticipantResponse>
    registerParticipant(
            @PathVariable Long tournamentId) {

        RegisterParticipantRequest request =
                new RegisterParticipantRequest();

        request.setTournamentId(tournamentId);

        String username = getAuthenticatedUsername();

        TournamentParticipantResponse response =
                service.registerParticipant(
                        request,
                        username
                );

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

    @DeleteMapping("/{tournamentId}/participants")
    public ResponseEntity<Void> withdrawParticipant(
            @PathVariable Long tournamentId) {

        String username = getAuthenticatedUsername();

        boolean withdrawn =
                service.withdrawParticipant(
                        tournamentId,
                        username
                );

        if (withdrawn) {
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