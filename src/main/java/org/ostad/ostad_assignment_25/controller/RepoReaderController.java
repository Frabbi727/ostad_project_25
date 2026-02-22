package org.ostad.ostad_assignment_25.controller;

import jakarta.validation.constraints.NotBlank;
import org.ostad.ostad_assignment_25.dto.BlogResponse;
import org.ostad.ostad_assignment_25.dto.ContributionsResponse;
import org.ostad.ostad_assignment_25.dto.IndicesResponse;
import org.ostad.ostad_assignment_25.service.RepositoryReadService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
public class RepoReaderController {

    private final RepositoryReadService repositoryReadService;

    public RepoReaderController(RepositoryReadService repositoryReadService) {
        this.repositoryReadService = repositoryReadService;
    }

    @GetMapping("/read_contributions")
    public ContributionsResponse readContributions() {
        return repositoryReadService.readContributions();
    }

    @GetMapping("/read_indices")
    public IndicesResponse readIndices() {
        return repositoryReadService.readIndices();
    }

    @GetMapping("/read_blog")
    public BlogResponse readBlog(
            @RequestParam("topic_name") @NotBlank String topicName,
            @RequestParam("sub_topic_name") @NotBlank String subTopicName
    ) {
        return repositoryReadService.readBlog(topicName, subTopicName);
    }
}
