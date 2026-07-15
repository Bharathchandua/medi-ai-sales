package com.medi.ai.sales.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // Find user by email (for password resets)
    Optional<User> findByEmail(String email);

    // Check if email already exists during registration
    boolean existsByEmail(String email);

    // Check if username already exists during registration
    boolean existsByUsername(String username);
}
