package org.ostad.ostad_assignment_25.repository;

import org.ostad.ostad_assignment_25.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    List<TopicEntity> findAllByOrderByOrderIndexAsc();
}
