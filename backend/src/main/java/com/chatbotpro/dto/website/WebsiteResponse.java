package com.chatbotpro.dto.website;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteResponse {
    private Long id;
    private String domain;
    private String name;
    private String botName;
    private String welcomeMessage;
    private String primaryColor;
    private String botToken;
    private boolean active;
    private LocalDateTime lastCrawledAt;
}
