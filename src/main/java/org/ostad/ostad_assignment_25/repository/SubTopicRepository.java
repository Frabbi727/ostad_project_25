package org.ostad.ostad_assignment_25.repository;

import org.ostad.ostad_assignment_25.entity.SubTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubTopicRepository extends JpaRepository<SubTopicEntity, Long> {
    Optional<SubTopicEntity> findByTopicNameIgnoreCaseAndNameIgnoreCase(String topicName, String subTopicName);

    List<SubTopicEntity> findAllByTopicNameOrderByOrderIndexAsc(String topicName);
}
