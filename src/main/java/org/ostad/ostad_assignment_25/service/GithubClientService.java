package org.ostad.ostad_assignment_25.service;

import org.ostad.ostad_assignment_25.config.GithubProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GithubClientService {

    private final GithubProperties properties;
    private final RestClient restClient;

    public GithubClientService(GithubProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .defaultHeaders(this::applyDefaultHeaders)
                .build();
    }

    public ReadmePayload fetchReadme() {
        String shaUrl = "%s/repos/%s/%s/contents/%s?ref=%s".formatted(
                properties.baseUrl(),
                properties.owner(),
                properties.repo(),
                properties.readmePath(),
                properties.branch()
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = restClient.get()
                .uri(shaUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Map.class);

        String sha = Objects.requireNonNull(metadata).getOrDefault("sha", "").toString();

        String rawUrl = "%s/%s/%s/%s/%s".formatted(
                properties.rawBaseUrl(),
                properties.owner(),
                properties.repo(),
                properties.branch(),
                properties.readmePath()
        );

        String markdown = restClient.get()
                .uri(rawUrl)
                .retrieve()
                .body(String.class);

        return new ReadmePayload(sha, markdown == null ? "" : markdown);
    }

    public List<ContributorPayload> fetchContributors() {
        String contributorsUrl = "%s/repos/%s/%s/contributors?per_page=100".formatted(
                properties.baseUrl(),
                properties.owner(),
                properties.repo()
        );

        List<Map<String, Object>> rows = restClient.get()
                .uri(contributorsUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(List.class);

        if (rows == null) {
            return List.of();
        }

        return rows.stream()
                .map(row -> new ContributorPayload(
                        row.getOrDefault("login", "").toString(),
                        Integer.parseInt(row.getOrDefault("contributions", 0).toString()),
                        row.getOrDefault("html_url", "").toString()
                ))
                .toList();
    }

    private void applyDefaultHeaders(HttpHeaders headers) {
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        if (properties.token() != null && !properties.token().isBlank()) {
            headers.setBearerAuth(properties.token());
        }
    }

    public record ReadmePayload(String sha, String markdown) {
    }

    public record ContributorPayload(String login, Integer contributions, String profileUrl) {
    }
}
