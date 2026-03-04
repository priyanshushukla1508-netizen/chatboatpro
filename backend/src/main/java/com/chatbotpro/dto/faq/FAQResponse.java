package com.chatbotpro.dto.faq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQResponse {
    private Long id;
    private String question;
    private String answer;
    private boolean isAIGenerated;
    private LocalDateTime createdAt;
}
