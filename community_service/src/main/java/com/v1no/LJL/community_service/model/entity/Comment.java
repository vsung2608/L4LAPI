package com.v1no.LJL.community_service.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_target", columnList = "target_type,target_id"),
    @Index(name = "idx_parent", columnList = "parent_id"),
    @Index(name = "idx_created", columnList = "created_at")
})
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private CommentTargetType targetType;
    
    @Column(name = "target_id", nullable = false)
    private Long targetId;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "reply_count", nullable = false)
    @Builder.Default
    private Integer replyCount = 0;
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Transient
    private List<Comment> replies;
}
