package com.chatbotpro.service;

import com.chatbotpro.client.GroqClient;
import com.chatbotpro.entity.ChatSession;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.ChatSessionRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Smart Follow-Up Email Bot
 *
 * After a chat session ends:
 * 1. Summarizes the conversation using Groq AI
 * 2. Generates a personalized follow-up email based on topics discussed
 * 3. Sends the email automatically to the visitor
 *
 * Example output:
 * "Hi Rahul! Thanks for chatting with us about our Enterprise plan.
 *  Here's a custom proposal based on your requirements..."
 */
@Service
@RequiredArgsConstructor
public class FollowUpEmailService {

    private final GroqClient groqClient;
    private final JavaMailSender mailSender;
    private final ChatSessionRepository sessionRepository;
    private final WebsiteRepository websiteRepository;

    /**
     * Called when a chat session ends (user closes chat or 10-min idle).
     * Generates and sends a personalized follow-up email.
     */
    public void sendFollowUpEmail(String botToken, String sessionId,
                                  String visitorName, String visitorEmail,
                                  List<String> conversationHistory) {
        if (visitorEmail == null || visitorEmail.isBlank()) return;

        Website website = websiteRepository.findByBotToken(botToken).orElse(null);
        if (website == null) return;

        try {
            // 1. Build conversation text for AI
            String conversationText = String.join("\n", conversationHistory);

            // 2. Generate personalized email using Groq AI
            String systemPrompt = """
                You are an expert sales & support email writer.
                Write a short, warm, personalized follow-up email based on this chat conversation.
                
                Rules:
                - Start with "Hi [Name]!" — use their actual name
                - Summarize the 1-2 key things they asked about
                - Include a clear, helpful next step (trial link, call booking, etc.)
                - Keep it under 150 words
                - Tone: friendly, professional, not pushy
                - End with: "Warm regards, Team ChatBotPro"
                - Use HTML formatting (bold key points)
                """;

            String userPrompt = String.format(
                    "Visitor Name: %s\nConversation:\n%s\n\nWrite the follow-up email:",
                    visitorName != null ? visitorName : "there",
                    conversationText
            );

            String emailBody = groqClient.chat(systemPrompt, userPrompt);

            // 3. Store session record
            ChatSession chatSession = ChatSession.builder()
                    .website(website)
                    .sessionId(sessionId)
                    .visitorName(visitorName)
                    .visitorEmail(visitorEmail)
                    .conversationSummary(conversationText.substring(0, Math.min(500, conversationText.length())))
                    .followUpEmailBody(emailBody)
                    .endedAt(LocalDateTime.now())
                    .build();

            // 4. Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(visitorEmail);
            helper.setSubject("Thanks for chatting with " + (website.getName() != null ? website.getName() : "us") + "!");
            helper.setText(emailBody, true); // true = HTML

            mailSender.send(message);
            chatSession.setEmailSent(true);
            sessionRepository.save(chatSession);

        } catch (Exception e) {
            System.err.println("Follow-up email failed: " + e.getMessage());
        }
    }
}
