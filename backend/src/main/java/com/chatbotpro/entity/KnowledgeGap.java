package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Stores knowledge gaps — questions users asked that the AI couldn't answer well.
 * Owner reviews these to improve bot training.
 */
@Entity
@Table(name = "knowledge_gaps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    @Column(nullable = false, length = 1000)
    private String question;          // The unanswered question

    private Integer occurrenceCount;  // How many times asked

    private Boolean resolved;         // Owner marked as trained

    private String suggestedAnswer;   // Owner can type the correct answer

    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;

    @PrePersist
    protected void onCreate() {
        firstSeenAt = LocalDateTime.now();
        lastSeenAt = LocalDateTime.now();
        occurrenceCount = 1;
        resolved = false;
    }
}
