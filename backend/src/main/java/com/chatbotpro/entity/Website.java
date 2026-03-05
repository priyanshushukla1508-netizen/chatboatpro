package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "websites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String name;

    private String botName;
    private String welcomeMessage;
    private String primaryColor;

    @Column(unique = true, nullable = false)
    private String botToken;

    @Builder.Default
    private String plan = "TRIAL";

    private boolean active;

    @Column(columnDefinition = "TEXT")
    private String scrapedContent;

    private LocalDateTime lastCrawledAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (botToken == null) {
            botToken = java.util.UUID.randomUUID().toString();
        }
    }
}
