package com.tournament.tournament_management_system.service;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}