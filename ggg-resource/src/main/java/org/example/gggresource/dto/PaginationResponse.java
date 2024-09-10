package org.example.gggresource.dto;

import java.util.List;

public record PaginationResponse(
        List<?> data,
        LinkResponse link
) {
}
