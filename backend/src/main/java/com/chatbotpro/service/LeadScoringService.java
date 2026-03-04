package com.chatbotpro.service;

import com.chatbotpro.entity.LeadScore;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.LeadScoreRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeadScoringService {

    private final LeadScoreRepository leadScoreRepository;
    private final WebsiteRepository websiteRepository;

    /**
     * Called from widget to update visitor behavior signals.
     * Recalculates score and tier every time.
     */
    public LeadScore updateScore(String botToken, String sessionId, Map<String, Object> behavior) {
        Website website = websiteRepository.findByBotToken(botToken).orElse(null);
        if (website == null) return null;

        LeadScore lead = leadScoreRepository.findByWebsiteAndSessionId(website, sessionId)
                .orElse(LeadScore.builder().website(website).sessionId(sessionId).build());

        // Update signals from behavior map
        if (behavior.containsKey("timeOnPageSeconds"))
            lead.setTimeOnPageSeconds((Integer) behavior.get("timeOnPageSeconds"));
        if (behavior.containsKey("messageCount"))
            lead.setMessageCount((Integer) behavior.get("messageCount"));
        if (behavior.containsKey("pagesVisited"))
            lead.setPagesVisited((Integer) behavior.get("pagesVisited"));
        if (behavior.containsKey("visitedPricing"))
            lead.setVisitedPricing((Boolean) behavior.get("visitedPricing"));
        if (behavior.containsKey("askedAboutEnterprise"))
            lead.setAskedAboutEnterprise((Boolean) behavior.get("askedAboutEnterprise"));
        if (behavior.containsKey("askedPrice"))
            lead.setAskedPrice((Boolean) behavior.get("askedPrice"));
        if (behavior.containsKey("visitorName"))
            lead.setVisitorName((String) behavior.get("visitorName"));
        if (behavior.containsKey("visitorEmail"))
            lead.setVisitorEmail((String) behavior.get("visitorEmail"));

        // === SCORING ALGORITHM ===
        int score = 0;

        // Time on page: max 20 pts (2 pts per 30s, max 10 min)
        int time = lead.getTimeOnPageSeconds() != null ? lead.getTimeOnPageSeconds() : 0;
        score += Math.min(20, (time / 30) * 2);

        // Messages sent: max 20 pts (4 pts each, max 5 messages)
        int msgs = lead.getMessageCount() != null ? lead.getMessageCount() : 0;
        score += Math.min(20, msgs * 4);

        // Pages visited: max 15 pts
        int pages = lead.getPagesVisited() != null ? lead.getPagesVisited() : 0;
        score += Math.min(15, pages * 5);

        // High-intent signals: 15 pts each
        if (Boolean.TRUE.equals(lead.getVisitedPricing())) score += 15;
        if (Boolean.TRUE.equals(lead.getAskedPrice())) score += 15;

        // Enterprise signal: 15 pts (highest value)
        if (Boolean.TRUE.equals(lead.getAskedAboutEnterprise())) score += 15;

        score = Math.min(100, score);
        lead.setScore(score);

        // Assign tier
        if (score >= 61) lead.setTier("HOT");
        else if (score >= 31) lead.setTier("WARM");
        else lead.setTier("COLD");

        return leadScoreRepository.save(lead);
    }

    /**
     * Dashboard: Get all leads sorted by score (hottest first).
     */
    public List<LeadScore> getLeadsForWebsite(Long websiteId) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));
        return leadScoreRepository.findAllByWebsiteOrderByScoreDesc(website);
    }
}
