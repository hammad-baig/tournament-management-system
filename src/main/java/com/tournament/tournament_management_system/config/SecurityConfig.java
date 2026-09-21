package com.tournament.tournament_management_system.config;

import com.tournament.tournament_management_system.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider) {

        return authenticationProvider::authenticate;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Login is publicly accessible
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Tournament management → ORGANIZER only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/tournaments"
                        )
                        .hasRole("ORGANIZER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/tournaments/**"
                        )
                        .hasRole("ORGANIZER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/tournaments/**"
                        )
                        .hasRole("ORGANIZER")

                        // Viewing all participants → ORGANIZER only
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/participants"
                        )
                        .hasRole("ORGANIZER")

                        // Viewing tournament participants → ORGANIZER only
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/tournaments/*/participants"
                        )
                        .hasRole("ORGANIZER")

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}