package com.chatbotpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    @Column(nullable = false)
    private String code;          // e.g. SAVE10

    @Column(nullable = false)
    private Integer discountPercent; // e.g. 10

    private String message;       // "Wait! Use code SAVE10 for 10% off!"

    private Integer timerSeconds; // Countdown timer (default 300 = 5 min)

    private boolean active;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timerSeconds == null) timerSeconds = 300;
        if (message == null) message = "Wait! Don't leave yet! Use code " + code + " for " + discountPercent + "% off in the next 5 minutes!";
    }
}
