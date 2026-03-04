package com.chatbotpro.service;

import com.chatbotpro.entity.KnowledgeGap;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.KnowledgeGapRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Knowledge Gap Detector — logs questions the AI couldn't answer.
 * Deduplicates gaps and increments occurrence count.
 * Owner reviews dashboard to improve training.
 */
@Service
@RequiredArgsConstructor
public class KnowledgeGapService {

    private final KnowledgeGapRepository gapRepository;
    private final WebsiteRepository websiteRepository;

    /**
     * Called from ChatService when AI response is low-confidence or fallback.
     * Logs the question as a knowledge gap.
     */
    public void logGap(Website website, String question) {
        try {
            Optional<KnowledgeGap> existing = gapRepository.findByWebsiteAndQuestion(website, question);
            if (existing.isPresent()) {
                // Increment count
                KnowledgeGap gap = existing.get();
                gap.setOccurrenceCount(gap.getOccurrenceCount() + 1);
                gap.setLastSeenAt(LocalDateTime.now());
                gapRepository.save(gap);
            } else {
                // New gap
                KnowledgeGap gap = KnowledgeGap.builder()
                        .website(website)
                        .question(question)
                        .build();
                gapRepository.save(gap);
            }
        } catch (Exception e) {
            System.err.println("Failed to log knowledge gap: " + e.getMessage());
        }
    }

    /**
     * Dashboard: Get all unresolved gaps sorted by frequency (most asked first).
     */
    public List<KnowledgeGap> getUnresolvedGaps(Long websiteId) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));
        return gapRepository.findAllByWebsiteAndResolvedFalseOrderByOccurrenceCountDesc(website);
    }

    /**
     * Owner marks a gap as resolved after training the bot with the answer.
     */
    public void resolveGap(Long gapId, String suggestedAnswer) {
        KnowledgeGap gap = gapRepository.findById(gapId)
                .orElseThrow(() -> new RuntimeException("Gap not found"));
        gap.setResolved(true);
        gap.setSuggestedAnswer(suggestedAnswer);
        gapRepository.save(gap);
    }
}
