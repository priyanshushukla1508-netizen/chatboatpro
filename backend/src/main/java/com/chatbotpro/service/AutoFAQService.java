package com.chatbotpro.service;

import com.chatbotpro.client.GroqClient;
import com.chatbotpro.entity.ChatMessage;
import com.chatbotpro.entity.FAQ;
import com.chatbotpro.entity.FAQSuggestion;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.ChatMessageRepository;
import com.chatbotpro.repository.FAQRepository;
import com.chatbotpro.repository.FAQSuggestionRepository;
import com.chatbotpro.repository.WebsiteRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoFAQService {

    private final ChatMessageRepository chatMessageRepository;
    private final FAQSuggestionRepository faqSuggestionRepository;
    private final FAQRepository faqRepository;
    private final WebsiteRepository websiteRepository;
    private final GroqClient groqClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void generateSuggestions(Long websiteId) {
        Website website = websiteRepository.findById(websiteId).orElseThrow();
        
        // 1. Get recent chat sessions
        List<ChatMessage> messages = chatMessageRepository.findAllByWebsite(website);
        if (messages.isEmpty()) return;

        String chatLog = messages.stream()
                .map(m -> m.getSenderType() + ": " + m.getMessage())
                .limit(100) // Limit to last 100 messages for analysis
                .collect(Collectors.joining("\n"));

        // 2. Build prompt for Groq
        String systemPrompt = "You are a Customer Experience Analyst. Analyze the chat logs and identify 3 recurring " +
                "questions from visitors that are not yet officially answered in their documentation. " +
                "Return the result ONLY as a JSON array of objects with 'question' and 'answer' fields. " +
                "Example: [{\"question\": \"...\", \"answer\": \"...\"}]";

        String response = groqClient.getCompletion(systemPrompt, "Chat logs to analyze:\n" + chatLog);

        // 3. Parse and save suggestions
        try {
            // Clean response if AI adds markdown backticks
            String cleanedJson = response.replaceAll("```json", "").replaceAll("```", "").trim();
            List<Map<String, String>> suggestions = objectMapper.readValue(cleanedJson, new TypeReference<List<Map<String, String>>>() {});
            
            for (Map<String, String> sug : suggestions) {
                FAQSuggestion suggestion = FAQSuggestion.builder()
                        .website(website)
                        .question(sug.get( "question"))
                        .answer(sug.get( "answer"))
                        .status(FAQSuggestion.SuggestionStatus.PENDING)
                        .build();
                faqSuggestionRepository.save(suggestion);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse Auto-FAQ suggestions: " + e.getMessage());
        }
    }

    public List<FAQSuggestion> getPendingSuggestions(Long websiteId) {
        Website website = websiteRepository.findById(websiteId).orElseThrow();
        return faqSuggestionRepository.findAllByWebsiteAndStatus(website, FAQSuggestion.SuggestionStatus.PENDING);
    }

    public void approveSuggestion(Long id) {
        FAQSuggestion suggestion = faqSuggestionRepository.findById(id).orElseThrow();
        
        // 1. Create real FAQ
        FAQ faq = FAQ.builder()
                .website(suggestion.getWebsite())
                .question(suggestion.getQuestion())
                .answer(suggestion.getAnswer())
                .isAIGenerated(true)
                .build();
        faqRepository.save(faq);

        // 2. Mark suggestion as approved
        suggestion.setStatus(FAQSuggestion.SuggestionStatus.APPROVED);
        faqSuggestionRepository.save(suggestion);
    }

    public void rejectSuggestion(Long id) {
        FAQSuggestion suggestion = faqSuggestionRepository.findById(id).orElseThrow();
        suggestion.setStatus(FAQSuggestion.SuggestionStatus.REJECTED);
        faqSuggestionRepository.save(suggestion);
    }
}
