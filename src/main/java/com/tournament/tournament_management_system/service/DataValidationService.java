package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.CreateParticipantRequest;
import com.tournament.tournament_management_system.dto.CreateTournamentRequest;
import com.tournament.tournament_management_system.dto.CreateUserRequest;
import com.tournament.tournament_management_system.dto.RegisterParticipantRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class DataValidationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    public void validateUser(CreateUserRequest request) {

        if (request == null) {
            throw new ValidationException("User request cannot be null");
        }

        validateRequiredText(
                request.getUsername(),
                "Username",
                3,
                50
        );

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new ValidationException("Email is required");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new ValidationException("Invalid email format");
        }

        if (request.getEmail().length() > 255) {
            throw new ValidationException(
                    "Email must not exceed 255 characters"
            );
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new ValidationException("Password is required");
        }

        if (request.getPassword().length() < 8) {
            throw new ValidationException(
                    "Password must contain at least 8 characters"
            );
        }

        if (request.getPassword().length() > 100) {
            throw new ValidationException(
                    "Password must not exceed 100 characters"
            );
        }

        if (request.getRole() == null) {
            throw new ValidationException("Role is required");
        }
    }

    public void validateTournament(
            CreateTournamentRequest request) {

        if (request == null) {
            throw new ValidationException(
                    "Tournament request cannot be null"
            );
        }

        validateRequiredText(
                request.getName(),
                "Tournament name",
                3,
                150
        );

        if (request.getDescription() != null &&
                request.getDescription().length() > 2000) {

            throw new ValidationException(
                    "Tournament description must not exceed 2000 characters"
            );
        }

        if (request.getStartDate() == null) {
            throw new ValidationException(
                    "Tournament start date is required"
            );
        }

        if (request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    "Tournament start date must be in the future"
            );
        }

        validateRequiredText(
                request.getLocation(),
                "Tournament location",
                2,
                255
        );

        if (request.getStatus() == null) {
            throw new ValidationException(
                    "Tournament status is required"
            );
        }

        validatePositiveId(
                request.getCreatedBy(),
                "Created by"
        );
    }

    public void validateParticipant(
            CreateParticipantRequest request) {

        if (request == null) {
            throw new ValidationException(
                    "Participant request cannot be null"
            );
        }

        validatePositiveId(
                request.getUserId(),
                "User ID"
        );

        validateRequiredText(
                request.getDisplayName(),
                "Display name",
                2,
                100
        );

        if (request.getRating() == null) {
            throw new ValidationException(
                    "Rating is required"
            );
        }

        if (request.getRating() < 0) {
            throw new ValidationException(
                    "Rating cannot be negative"
            );
        }

        if (request.getCountry() != null &&
                request.getCountry().length() > 100) {

            throw new ValidationException(
                    "Country must not exceed 100 characters"
            );
        }
    }

    public void validateRegistration(
            RegisterParticipantRequest request) {

        if (request == null) {
            throw new ValidationException(
                    "Registration request cannot be null"
            );
        }

        validatePositiveId(
                request.getTournamentId(),
                "Tournament ID"
        );

        validatePositiveId(
                request.getParticipantId(),
                "Participant ID"
        );
    }

    private void validateRequiredText(
            String value,
            String fieldName,
            int minimumLength,
            int maximumLength) {

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName + " is required"
            );
        }

        String trimmedValue = value.trim();

        if (trimmedValue.length() < minimumLength) {
            throw new ValidationException(
                    fieldName + " must contain at least "
                            + minimumLength + " characters"
            );
        }

        if (trimmedValue.length() > maximumLength) {
            throw new ValidationException(
                    fieldName + " must not exceed "
                            + maximumLength + " characters"
            );
        }
    }

    private void validatePositiveId(
            Long id,
            String fieldName) {

        if (id == null) {
            throw new ValidationException(
                    fieldName + " is required"
            );
        }

        if (id <= 0) {
            throw new ValidationException(
                    fieldName + " must be greater than 0"
            );
        }
    }
}