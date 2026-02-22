package org.ostad.ostad_assignment_25.dto;

import java.time.Instant;
import java.util.List;

public record ContributionsResponse(
        String repository,
        Instant lastSyncedAt,
        List<ContributorDto> contributors
) {
}
