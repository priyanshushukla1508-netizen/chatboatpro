package com.chatbotpro.service;

import com.chatbotpro.dto.dashboard.DashboardResponse;
import com.chatbotpro.entity.ChatMessage;
import com.chatbotpro.entity.User;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.ChatMessageRepository;
import com.chatbotpro.repository.FAQRepository;
import com.chatbotpro.repository.UserRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WebsiteRepository websiteRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final FAQRepository faqRepository;
    private final UserRepository userRepository;

    public DashboardResponse getStats() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email).orElseThrow();

        List<Website> websites = websiteRepository.findAllByOwner(owner);
        long totalWebsites = websites.size();
        long totalChats = 0;
        long totalFAQs = 0;

        for (Website website : websites) {
            // This is a simplified count. For production, use optimized queries.
            totalFAQs += faqRepository.findAllByWebsite(website).size();
        }

        return DashboardResponse.builder()
                .totalWebsites(totalWebsites)
                .totalChats(totalChats) // Placeholder: Need a better way to count chats across sites
                .totalFAQs(totalFAQs)
                .totalLeads(0) // Placeholder for future lead capture feature
                .build();
    }

    public ChatMessage sendAdminResponse(Long websiteId, String sessionId, String message) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));
        
        ChatMessage adminMsg = ChatMessage.builder()
                .website(website)
                .sessionId(sessionId)
                .message(message)
                .senderType("ADMIN")
                .build();
        
        return chatMessageRepository.save(adminMsg);
    }

    public ChatMessage sendInternalNote(Long websiteId, String sessionId, String note) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));
        
        ChatMessage noteMsg = ChatMessage.builder()
                .website(website)
                .sessionId(sessionId)
                .message(note)
                .senderType("INTERNAL_NOTE")
                .build();
        
        return chatMessageRepository.save(noteMsg);
    }
}
