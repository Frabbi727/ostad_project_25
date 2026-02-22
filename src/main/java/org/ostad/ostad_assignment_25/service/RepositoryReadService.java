package org.ostad.ostad_assignment_25.service;

import org.ostad.ostad_assignment_25.config.GithubProperties;
import org.ostad.ostad_assignment_25.dto.BlogResponse;
import org.ostad.ostad_assignment_25.dto.ContributionsResponse;
import org.ostad.ostad_assignment_25.dto.ContributorDto;
import org.ostad.ostad_assignment_25.dto.IndicesResponse;
import org.ostad.ostad_assignment_25.dto.SubTopicIndexDto;
import org.ostad.ostad_assignment_25.dto.TopicIndexDto;
import org.ostad.ostad_assignment_25.entity.SyncStateEntity;
import org.ostad.ostad_assignment_25.entity.TopicEntity;
import org.ostad.ostad_assignment_25.exception.ResourceNotFoundException;
import org.ostad.ostad_assignment_25.repository.ContributorRepository;
import org.ostad.ostad_assignment_25.repository.SubTopicRepository;
import org.ostad.ostad_assignment_25.repository.SyncStateRepository;
import org.ostad.ostad_assignment_25.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RepositoryReadService {

    private final ContributorRepository contributorRepository;
    private final TopicRepository topicRepository;
    private final SubTopicRepository subTopicRepository;
    private final SyncStateRepository syncStateRepository;
    private final GithubProperties githubProperties;

    public RepositoryReadService(
            ContributorRepository contributorRepository,
            TopicRepository topicRepository,
            SubTopicRepository subTopicRepository,
            SyncStateRepository syncStateRepository,
            GithubProperties githubProperties
    ) {
        this.contributorRepository = contributorRepository;
        this.topicRepository = topicRepository;
        this.subTopicRepository = subTopicRepository;
        this.syncStateRepository = syncStateRepository;
        this.githubProperties = githubProperties;
    }

    public ContributionsResponse readContributions() {
        List<ContributorDto> contributors = contributorRepository.findAll().stream()
                .sorted(Comparator.comparingInt(c -> -c.getContributions()))
                .map(c -> new ContributorDto(c.getLogin(), c.getContributions(), c.getProfileUrl()))
                .toList();

        return new ContributionsResponse(repositoryName(), lastSyncedAt(), contributors);
    }

    public IndicesResponse readIndices() {
        List<TopicIndexDto> indices = topicRepository.findAllByOrderByOrderIndexAsc().stream()
                .map(this::toTopicIndex)
                .toList();

        return new IndicesResponse(repositoryName(), lastSyncedAt(), indices);
    }

    public BlogResponse readBlog(String topicName, String subTopicName) {
        var subTopic = subTopicRepository.findByTopicNameIgnoreCaseAndNameIgnoreCase(topicName, subTopicName)
                .orElseThrow(() -> new ResourceNotFoundException("Topic/sub-topic not found"));

        return new BlogResponse(
                subTopic.getTopic().getName(),
                subTopic.getName(),
                subTopic.getContent(),
                subTopic.getTopic().getUpdatedAt()
        );
    }

    private TopicIndexDto toTopicIndex(TopicEntity topic) {
        List<SubTopicIndexDto> subTopicIndices = subTopicRepository.findAllByTopicNameOrderByOrderIndexAsc(topic.getName())
                .stream()
                .map(s -> new SubTopicIndexDto(s.getName(), s.getOrderIndex()))
                .toList();

        return new TopicIndexDto(topic.getName(), topic.getOrderIndex(), subTopicIndices);
    }

    private Instant lastSyncedAt() {
        return syncStateRepository.findById(1L)
                .map(SyncStateEntity::getLastSyncedAt)
                .orElse(Instant.EPOCH);
    }

    private String repositoryName() {
        return githubProperties.owner() + "/" + githubProperties.repo();
    }
}
