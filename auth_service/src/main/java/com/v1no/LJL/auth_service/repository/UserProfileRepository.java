package com.v1no.LJL.auth_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.v1no.LJL.auth_service.model.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    @Query("SELECT p FROM UserProfile p LEFT JOIN FETCH p.userCredential WHERE p.id = :id")
    Optional<UserProfile> findByIdWithCredential(UUID id);

    @Query("SELECT p FROM UserProfile p WHERE p.isVip = true AND (p.vipExpiresAt IS NULL OR p.vipExpiresAt > :now)")
    List<UserProfile> findActiveVipUsers(LocalDateTime now);

    @Query("SELECT p FROM UserProfile p WHERE p.isVip = true AND p.vipExpiresAt IS NOT NULL AND p.vipExpiresAt < :now")
    List<UserProfile> findExpiredVipUsers(LocalDateTime now);

    @Query("SELECT COUNT(p) FROM UserProfile p WHERE p.isVip = true AND (p.vipExpiresAt IS NULL OR p.vipExpiresAt > :now)")
    long countActiveVipUsers(LocalDateTime now);

    @Query("SELECT p FROM UserProfile p WHERE p.currentStreakDays >= :minStreak ORDER BY p.currentStreakDays DESC")
    List<UserProfile> findUsersWithStreak(int minStreak);

    @Query("SELECT p FROM UserProfile p ORDER BY p.totalLessonsCompleted DESC")
    List<UserProfile> findTopLearners(org.springframework.data.domain.Pageable pageable);
}
