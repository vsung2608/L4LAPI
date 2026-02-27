package com.v1no.LJL.learning_service.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id
    @Column(name = "code", length = 10)
    private String code; // ja, zh, en, ko

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "native_name", nullable = false, length = 100)
    private String nativeName;

    @Column(name = "flag_url")
    private String flagUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContentStatus status = ContentStatus.ACTIVE;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();
}
