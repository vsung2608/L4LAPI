package com.v1no.LJL.community_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "group_messages", indexes = {
    @Index(name = "idx_group_message_group", columnList = "group_id, created_at"),
    @Index(name = "idx_group_message_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ChatGroup group;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "message_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MessageType {
        TEXT,
        IMAGE,
        SYSTEM
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.content = "[Tin nhắn đã bị xóa]";
    }

    public boolean isSystemMessage() {
        return MessageType.SYSTEM.equals(messageType);
    }

    public boolean canEdit(UUID userId) {
        return this.userId.equals(userId) && !isDeleted && !isSystemMessage();
    }

    public boolean canDelete(UUID userId, boolean isModerator) {
        return this.userId.equals(userId) || isModerator;
    }
}