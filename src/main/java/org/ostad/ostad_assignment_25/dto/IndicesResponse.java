package org.ostad.ostad_assignment_25.dto;

import java.time.Instant;
import java.util.List;

public record IndicesResponse(
        String repository,
        Instant lastSyncedAt,
        List<TopicIndexDto> indices
) {
}
