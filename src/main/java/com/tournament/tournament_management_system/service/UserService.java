package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.CreateUserRequest;
import com.tournament.tournament_management_system.dto.UserResponse;
import com.tournament.tournament_management_system.model.User;
import com.tournament.tournament_management_system.model.UserRole;
import com.tournament.tournament_management_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataValidationService validationService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            DataValidationService validationService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    public Optional<UserResponse> findByUsername(String username) {

        return userRepository.findByUsername(username)
                .map(this::toUserResponse);
    }

    public UserResponse createUser(CreateUserRequest request) {

        validationService.validateUser(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        // New users are always created as participants.
        user.setRole(UserRole.PARTICIPANT);

        User savedUser = userRepository.createUser(user);

        return toUserResponse(savedUser);
    }

    private UserResponse toUserResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }
}