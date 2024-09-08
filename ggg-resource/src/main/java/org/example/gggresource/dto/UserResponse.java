package org.example.gggresource.dto;

public record UserResponse(
        boolean success,
        long id,
        String username) {
}
