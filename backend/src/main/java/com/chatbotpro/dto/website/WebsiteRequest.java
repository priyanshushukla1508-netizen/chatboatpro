package com.chatbotpro.dto.website;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteRequest {
    private String domain;
    private String name;
    private String botName;
    private String welcomeMessage;
    private String primaryColor;
}
