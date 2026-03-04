package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Stores ended chat conversations for the follow-up email bot.
 * After a session ends, AI generates and sends a personalized email.
 */
@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    private String sessionId;

    private String visitorName;
    private String visitorEmail;

    @Column(columnDefinition = "TEXT")
    private String conversationSummary;   // AI-generated summary

    @Column(columnDefinition = "TEXT")
    private String followUpEmailBody;     // AI-generated email

    private Boolean emailSent;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        emailSent = false;
    }
}
