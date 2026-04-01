package com.v1no.LJL.community_service.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ratings",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"user_id", "target_type", "target_id"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private CommentTargetType targetType;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 1000)
    private String review;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}