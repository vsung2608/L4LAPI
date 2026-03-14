package com.v1no.ljl.progress_service.repository;

import com.v1no.ljl.progress_service.model.entity.UserCardRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCardRecordRepository extends JpaRepository<UserCardRecord, UUID> {

    Optional<UserCardRecord> findByUserDeckProgressIdAndFlashCardId(
            UUID userDeckProgressId, UUID flashCardId);
}
