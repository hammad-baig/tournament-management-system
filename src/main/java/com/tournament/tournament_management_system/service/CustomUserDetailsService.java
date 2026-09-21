package com.tournament.tournament_management_system.service;

import com.tournament.tournament_management_system.model.User;
import com.tournament.tournament_management_system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        System.out.println("🔥 Looking for username: [" + username + "]");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
//                    System.out.println("❌ USER NOT FOUND IN DATABASE");
                    return new UsernameNotFoundException(
                            "User not found: " + username
                    );
                });

//        System.out.println("✅ USER FOUND: " + user.getUsername());
//        System.out.println("✅ ROLE: " + user.getRole());
//        System.out.println("✅ HASH: " + user.getPasswordHash());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}