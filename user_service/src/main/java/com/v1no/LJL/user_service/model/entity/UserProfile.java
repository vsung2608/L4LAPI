package com.v1no.LJL.user_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v1no.LJL.user_service.model.enums.Gender;
import com.v1no.LJL.user_service.model.enums.JLPTLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", indexes = {
    @Index(name = "idx_profile_level", columnList = "current_level"),
    @Index(name = "idx_profile_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
    name = "UserProfile.withSettings",
    attributeNodes = @NamedAttributeNode("settings")
)
@NamedEntityGraph(
    name = "UserProfile.withStats",
    attributeNodes = @NamedAttributeNode("studyStats")
)
@NamedEntityGraph(
    name = "UserProfile.full",
    attributeNodes = {
        @NamedAttributeNode("settings"),
        @NamedAttributeNode("studyStats")
    }
)
public class UserProfile {

    @Id
    private UUID id;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "native_language", length = 10)
    @Builder.Default
    private String nativeLanguage = "vi";

    @Column(name = "current_level", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JLPTLevel currentLevel = JLPTLevel.N5;

    @Column(length = 50)
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserSettings settings;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserStudyStats studyStats;
}
