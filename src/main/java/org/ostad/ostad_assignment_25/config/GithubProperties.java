package org.ostad.ostad_assignment_25.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.github")
public record GithubProperties(
        String owner,
        String repo,
        String branch,
        String readmePath,
        String baseUrl,
        String rawBaseUrl,
        String token
) {
}
