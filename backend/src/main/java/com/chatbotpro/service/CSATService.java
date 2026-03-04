package com.chatbotpro.service;

import com.chatbotpro.entity.CSATRating;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.CSATRatingRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CSATService {

    private final CSATRatingRepository csatRatingRepository;
    private final WebsiteRepository websiteRepository;

    /**
     * Widget calls this after user submits star rating.
     */
    public CSATRating submitRating(String botToken, String sessionId, int rating, String feedback) {
        Website website = websiteRepository.findByBotToken(botToken)
                .orElseThrow(() -> new RuntimeException("Invalid bot token"));

        CSATRating csatRating = CSATRating.builder()
                .website(website)
                .sessionId(sessionId)
                .rating(Math.min(5, Math.max(1, rating))) // clamp 1-5
                .feedback(feedback)
                .build();

        return csatRatingRepository.save(csatRating);
    }

    /**
     * Dashboard calls this to get CSAT summary for a website.
     */
    public Map<String, Object> getSummary(Long websiteId) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));

        Double avg = csatRatingRepository.findAverageRatingByWebsite(website);
        long total = csatRatingRepository.findAllByWebsite(website).size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        summary.put("totalRatings", total);
        return summary;
    }
}
