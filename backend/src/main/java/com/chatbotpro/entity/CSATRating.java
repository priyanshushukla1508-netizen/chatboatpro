package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "csat_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CSATRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    private String sessionId;

    private Integer rating; // 1 to 5 stars

    private String feedback; // Optional text

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
