package com.chatbotpro.service;

import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapedContentService {

    private final WebsiteRepository websiteRepository;

    /**
     * Saves scraped text directly into the Website entity in the database.
     */
    public void saveScrapedContent(Long websiteId, String url, String content) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found: " + websiteId));
        
        // Append or replace content. For now, we replace to keep it simple.
        website.setScrapedContent(content);
        websiteRepository.save(website);
    }

    /**
     * Retrieves scraped content from the database.
     */
    public String getContentForWebsite(Long websiteId) {
        return websiteRepository.findById(websiteId)
                .map(Website::getScrapedContent)
                .orElse("");
    }
}
