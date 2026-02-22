package org.ostad.ostad_assignment_25.dto;

public record ContributorDto(
        String login,
        Integer contributions,
        String profileUrl
) {
}
