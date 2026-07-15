package com.medi.ai.sales.security;

public record AuthResponse(
        String token,
        String username,
        Role role
) {
}
