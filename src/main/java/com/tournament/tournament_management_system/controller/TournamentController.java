package com.tournament.tournament_management_system.controller;

import com.tournament.tournament_management_system.dto.CreateTournamentRequest;
import com.tournament.tournament_management_system.dto.TournamentResponse;
import com.tournament.tournament_management_system.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @RequestBody CreateTournamentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tournamentService.createTournament(
                        request.getCreatedBy(),
                        request
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournamentById(
            @PathVariable Long id) {

        return tournamentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {

        return ResponseEntity.ok(
                tournamentService.findAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable Long id,
            @RequestBody CreateTournamentRequest request) {

        return tournamentService.updateTournament(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(
            @PathVariable Long id) {

        if (tournamentService.deleteTournament(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}