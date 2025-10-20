package com.example.idsaregistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager; // <-- add this import
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UsersConfig {

    // pretend-user directory mapping usernames to connector endpoints
    private static final Map<String, String> USER_CONNECTOR_ENDPOINTS = new ConcurrentHashMap<>();

    static {
        USER_CONNECTOR_ENDPOINTS.put("alice", "https://connector.alice.example/endpoint");
        USER_CONNECTOR_ENDPOINTS.put("bob",   "https://connector.bob.example/endpoint");
    }

    // Make this PUBLIC so SecurityConfig can call it
    public static String connectorEndpointFor(String username) {
        return USER_CONNECTOR_ENDPOINTS.getOrDefault(username, "https://connector.default/endpoint");
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        User.UserBuilder b = User.withUsername("ignored").passwordEncoder(encoder::encode).roles("USER");
        UserDetails alice = b.username("alice").password("password").build();
        UserDetails bob   = b.username("bob").password("password").build();
        return new InMemoryUserDetailsManager(alice, bob);
    }
}