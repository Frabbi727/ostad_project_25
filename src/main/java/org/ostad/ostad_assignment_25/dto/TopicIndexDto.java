package org.ostad.ostad_assignment_25.dto;

import java.util.List;

public record TopicIndexDto(
        String topicName,
        Integer topicOrder,
        List<SubTopicIndexDto> subTopics
) {
}
