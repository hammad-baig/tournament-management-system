package com.tournament.tournament_management_system.service;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}