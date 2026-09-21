package com.tournament.tournament_management_system.controller;

import com.tournament.tournament_management_system.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        System.out.println("🔥 LOGIN ENDPOINT REACHED");

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );

            System.out.println("🔥 AUTHENTICATION SUCCESSFUL");

            return "Login successful for: " + authentication.getName();

        } catch (Exception exception) {

            System.out.println("🔥 AUTHENTICATION FAILED");
            System.out.println("Exception: " + exception.getClass().getName());
            System.out.println("Message: " + exception.getMessage());

            throw exception;
        }
    }
}