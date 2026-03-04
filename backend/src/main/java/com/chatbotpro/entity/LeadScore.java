package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Tracks visitor behavior for predictive lead scoring.
 * Score: 0-100 → Cold(0-30), Warm(31-60), Hot(61-100)
 */
@Entity
@Table(name = "lead_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id")
    private Website website;

    private String sessionId;

    private String visitorName;
    private String visitorEmail;
    private String visitorPhone;

    // Behavior signals
    private Integer timeOnPageSeconds;   // More time = more interest
    private Integer messageCount;         // More messages = engaged
    private Integer pagesVisited;         // More pages = researching
    private Boolean visitedPricing;       // Visited pricing = buying intent
    private Boolean askedAboutEnterprise; // Enterprise query = high value
    private Boolean askedPrice;           // Asked price = serious buyer

    // Computed
    private Integer score;               // 0-100
    private String tier;                 // HOT / WARM / COLD

    private Boolean alertSent;           // Email alert sent to owner

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        alertSent = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
