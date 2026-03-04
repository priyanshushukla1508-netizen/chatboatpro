package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "faq_suggestions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SuggestionStatus status = SuggestionStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum SuggestionStatus {
        PENDING, APPROVED, REJECTED
    }
}
