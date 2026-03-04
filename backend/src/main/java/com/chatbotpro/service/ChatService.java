package com.chatbotpro.service;

import com.chatbotpro.client.GroqClient;
import com.chatbotpro.dto.chat.ChatRequest;
import com.chatbotpro.dto.chat.ChatResponse;
import com.chatbotpro.entity.ChatMessage;
import com.chatbotpro.entity.FAQ;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.ChatMessageRepository;
import com.chatbotpro.repository.FAQRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final GroqClient groqClient;
    private final WebsiteRepository websiteRepository;
    private final FAQRepository faqRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ScrapedContentService scrapedContentService;
    private final ObjectionHandlerService objectionHandlerService;
    private final LanguageDetectionService languageDetectionService;

    // Number of past messages to include as context (4 exchanges = 8 messages)
    private static final int MEMORY_WINDOW = 8;

    public ChatResponse getChatResponse(ChatRequest request) {
        Website website = websiteRepository.findByBotToken(request.getBotToken())
                .orElseThrow(() -> new RuntimeException("Invalid bot token"));

        // === FEATURE 6: MULTILINGUAL AUTO-DETECTION ===
        LanguageDetectionService.Language detectedLang =
                languageDetectionService.detect(request.getMessage());
        String languageInstruction = languageDetectionService.getLanguageInstruction(detectedLang);
        boolean isHindi = languageDetectionService.isHindi(detectedLang);

        // === FEATURE: OBJECTION HANDLER (language-aware) ===
        String objectionReply = objectionHandlerService.handleObjection(request.getMessage(), isHindi);
        if (objectionReply != null) {
            // Objection caught — save and return immediately (skip Groq call)
            ChatMessage visitorMsg = ChatMessage.builder()
                    .website(website).sessionId(request.getSessionId())
                    .message(request.getMessage()).senderType("VISITOR").build();
            chatMessageRepository.save(visitorMsg);

            ChatMessage botMsg = ChatMessage.builder()
                    .website(website).sessionId(request.getSessionId())
                    .message(objectionReply).senderType("BOT").build();
            chatMessageRepository.save(botMsg);

            return ChatResponse.builder()
                    .response(objectionReply)
                    .sessionId(request.getSessionId())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        // 1. Store visitor message
        ChatMessage visitorMsg = ChatMessage.builder()
                .website(website)
                .sessionId(request.getSessionId())
                .message(request.getMessage())
                .senderType("VISITOR")
                .build();
        chatMessageRepository.save(visitorMsg);

        // 2. Build context from FAQs
        List<FAQ> faqs = faqRepository.findAllByWebsite(website);
        String faqContext = faqs.stream()
                .map(faq -> "Q: " + faq.getQuestion() + "\nA: " + faq.getAnswer())
                .collect(Collectors.joining("\n\n"));

        // 3. Build context from Scraped Content
        String scrapedContext = scrapedContentService.getContentForWebsite(website.getId());

        // 4. Combine contexts
        String fullContext = "--- FAQ KNOWLEDGE ---\n" + faqContext +
                           "\n\n--- WEBSITE CONTENT ---\n" + scrapedContext;

        // 5. Build system prompt — now includes language instruction
        String systemPrompt = String.format(
                "You are an AI assistant for the website: %s (Domain: %s).\n" +
                "Your name is: %s.\n" +
                "Use the following Q&A and website content context to answer the user's question.\n" +
                "Respond like a helpful, friendly human. If you don't know the answer, say you will connect them with a human.\n\n" +
                "%s\n\n" +   // <-- language instruction injected here
                "CONTEXT:\n%s",
                website.getName(), website.getDomain(), website.getBotName(),
                languageInstruction,
                fullContext
        );

        // === FEATURE 4: CONVERSATION MEMORY (Multi-turn Context) ===
        // Fetch last MEMORY_WINDOW messages from DB for this session (excluding the one just saved)
        List<ChatMessage> recentMessages = chatMessageRepository
                .findAllBySessionIdOrderByCreatedAtAsc(request.getSessionId());

        // Convert to Groq message format (excluding the current visitor message we just saved)
        List<Map<String, String>> conversationHistory = new ArrayList<>();
        int start = Math.max(0, recentMessages.size() - MEMORY_WINDOW - 1);
        for (int i = start; i < recentMessages.size() - 1; i++) {
            ChatMessage msg = recentMessages.get(i);
            if ("VISITOR".equals(msg.getSenderType())) {
                Map<String, String> historyMsg = new HashMap<>();
                historyMsg.put("role", "user");
                historyMsg.put("content", msg.getMessage());
                conversationHistory.add(historyMsg);
            } else if ("BOT".equals(msg.getSenderType())) {
                Map<String, String> historyMsg = new HashMap<>();
                historyMsg.put("role", "assistant");
                historyMsg.put("content", msg.getMessage());
                conversationHistory.add(historyMsg);
            }
            // Skip ADMIN/INTERNAL_NOTE messages
        }

        // 6. Get completion from Groq WITH conversation history
        String aiResponse = groqClient.getCompletionWithHistory(
                systemPrompt, conversationHistory, request.getMessage());

        // 7. Handle Trial Limits (150 messages slowness)
        long messageCount = chatMessageRepository.countByWebsiteAndSenderType(website, "VISITOR");
        if ("TRIAL".equals(website.getPlan()) && messageCount > 150) {
            try {
                Thread.sleep(5000);
                aiResponse += "\n\n⚠️ (Note: Aapke free trial ke 150 messages pure ho chuke hain, isliye speed kam ho gayi hai. Unlimited fast replies ke liye please Pro Plan lijiye!)";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 8. Store bot response
        ChatMessage botMsg = ChatMessage.builder()
                .website(website)
                .sessionId(request.getSessionId())
                .message(aiResponse)
                .senderType("BOT")
                .build();
        chatMessageRepository.save(botMsg);

        return ChatResponse.builder()
                .response(aiResponse)
                .sessionId(request.getSessionId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<ChatMessage> getChatHistoryForWidget(String sessionId) {
        return chatMessageRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .filter(m -> !"INTERNAL_NOTE".equals(m.getSenderType()))
                .collect(Collectors.toList());
    }
}
