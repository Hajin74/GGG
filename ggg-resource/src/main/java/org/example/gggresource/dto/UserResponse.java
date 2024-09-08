package org.example.gggresource.dto;

public record AuthResponse(
        boolean success,
        long id,
        String username) {
}
