package org.ostad.ostad_assignment_25.service;

import org.ostad.ostad_assignment_25.entity.ContributorEntity;
import org.ostad.ostad_assignment_25.entity.SubTopicEntity;
import org.ostad.ostad_assignment_25.entity.SyncStateEntity;
import org.ostad.ostad_assignment_25.entity.TopicEntity;
import org.ostad.ostad_assignment_25.repository.ContributorRepository;
import org.ostad.ostad_assignment_25.repository.SubTopicRepository;
import org.ostad.ostad_assignment_25.repository.SyncStateRepository;
import org.ostad.ostad_assignment_25.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RepositorySyncService {

    private static final Logger log = LoggerFactory.getLogger(RepositorySyncService.class);
    private static final long STATE_ID = 1L;

    private final GithubClientService githubClientService;
    private final MarkdownReadmeParser markdownReadmeParser;
    private final ContributorRepository contributorRepository;
    private final TopicRepository topicRepository;
    private final SubTopicRepository subTopicRepository;
    private final SyncStateRepository syncStateRepository;

    public RepositorySyncService(
            GithubClientService githubClientService,
            MarkdownReadmeParser markdownReadmeParser,
            ContributorRepository contributorRepository,
            TopicRepository topicRepository,
            SubTopicRepository subTopicRepository,
            SyncStateRepository syncStateRepository
    ) {
        this.githubClientService = githubClientService;
        this.markdownReadmeParser = markdownReadmeParser;
        this.contributorRepository = contributorRepository;
        this.topicRepository = topicRepository;
        this.subTopicRepository = subTopicRepository;
        this.syncStateRepository = syncStateRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartupSync() {
        runSafeSync();
    }

    @Scheduled(cron = "${app.sync.cron}")
    public void scheduledSync() {
        runSafeSync();
    }

    public void runSafeSync() {
        try {
            synchronizeNow();
        } catch (Exception ex) {
            log.error("Sync failed: {}", ex.getMessage(), ex);
        }
    }

    @Transactional
    public void synchronizeNow() {
        log.info("Sync started");

        GithubClientService.ReadmePayload readmePayload = githubClientService.fetchReadme();
        List<GithubClientService.ContributorPayload> contributors = githubClientService.fetchContributors();

        SyncStateEntity state = syncStateRepository.findById(STATE_ID).orElseGet(() -> {
            SyncStateEntity entity = new SyncStateEntity();
            entity.setId(STATE_ID);
            entity.setLastSyncedAt(Instant.EPOCH);
            return entity;
        });

        if (!readmePayload.sha().isBlank() && readmePayload.sha().equals(state.getLastReadmeSha())) {
            upsertContributors(contributors);
            state.setLastSyncedAt(Instant.now());
            syncStateRepository.save(state);
            log.info("Sync complete (README unchanged)");
            return;
        }

        List<MarkdownReadmeParser.ParsedTopic> topics = markdownReadmeParser.parse(readmePayload.markdown());

        subTopicRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();

        Instant now = Instant.now();

        for (MarkdownReadmeParser.ParsedTopic topic : topics) {
            TopicEntity topicEntity = new TopicEntity();
            topicEntity.setName(topic.name());
            topicEntity.setOrderIndex(topic.order());
            topicEntity.setUpdatedAt(now);
            TopicEntity savedTopic = topicRepository.save(topicEntity);

            for (MarkdownReadmeParser.ParsedSubTopic subTopic : topic.subTopics()) {
                SubTopicEntity subTopicEntity = new SubTopicEntity();
                subTopicEntity.setTopic(savedTopic);
                subTopicEntity.setName(subTopic.name());
                subTopicEntity.setOrderIndex(subTopic.order());
                subTopicEntity.setContent(subTopic.content());
                subTopicRepository.save(subTopicEntity);
            }
        }

        upsertContributors(contributors);

        state.setLastReadmeSha(readmePayload.sha());
        state.setLastSyncedAt(now);
        syncStateRepository.save(state);
        log.info("Sync complete with {} topics", topics.size());
    }

    private void upsertContributors(List<GithubClientService.ContributorPayload> contributors) {
        contributorRepository.deleteAllInBatch();

        for (GithubClientService.ContributorPayload contributor : contributors) {
            ContributorEntity entity = new ContributorEntity();
            entity.setLogin(contributor.login());
            entity.setContributions(contributor.contributions());
            entity.setProfileUrl(contributor.profileUrl());
            contributorRepository.save(entity);
        }
    }
}
