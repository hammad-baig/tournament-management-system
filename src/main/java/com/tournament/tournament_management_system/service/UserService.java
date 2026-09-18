package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.dto.CreateUserRequest;
import com.tournament.tournament_management_system.model.User;
import com.tournament.tournament_management_system.dto.UserResponse;
import com.tournament.tournament_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserResponse> findByUsername(String username) {

        return userRepository.findByUsername(username)
                .map(this::toUserResponse);
    }

    public UserResponse createUser(CreateUserRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Temporary for now.
        // Password hashing will be handled when we implement Spring Security.
        user.setPasswordHash(request.getPassword());

        user.setRole(request.getRole());

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