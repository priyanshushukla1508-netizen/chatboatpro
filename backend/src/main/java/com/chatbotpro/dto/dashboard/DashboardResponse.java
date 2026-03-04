package com.chatbotpro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long totalWebsites;
    private long totalChats;
    private long totalFAQs;
    private long totalLeads;
}
