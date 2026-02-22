package org.ostad.ostad_assignment_25.dto;

import java.time.Instant;

public record BlogResponse(
        String topicName,
        String subTopicName,
        String content,
        Instant updatedAt
) {
}
