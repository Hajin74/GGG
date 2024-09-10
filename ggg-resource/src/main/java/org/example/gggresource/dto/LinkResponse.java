package org.example.gggresource.dto;

public record LinkResponse(
        String previous,
        String next,
        long currentPage,
        int totalPage,
        long totalItems
) {
}
