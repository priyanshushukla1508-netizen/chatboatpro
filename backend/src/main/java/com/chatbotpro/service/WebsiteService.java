package com.chatbotpro.service;

import com.chatbotpro.dto.website.WebsiteRequest;
import com.chatbotpro.dto.website.WebsiteResponse;
import com.chatbotpro.entity.User;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.UserRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebsiteService {

    private final WebsiteRepository websiteRepository;
    private final UserRepository userRepository;
    private final WebCrawlerService webCrawlerService;
    private final ScrapedContentService scrapedContentService;

    public WebsiteResponse createWebsite(WebsiteRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email).orElseThrow();

        if (websiteRepository.countByOwner(owner) >= 1) {
            throw new RuntimeException("You have already created a chat bot");
        }

        Website website = Website.builder()
                .owner(owner)
                .domain(request.getDomain())
                .name(request.getName())
                .botName(request.getBotName())
                .welcomeMessage(request.getWelcomeMessage())
                .primaryColor(request.getPrimaryColor())
                .botToken(UUID.randomUUID().toString())
                .active(true)
                .build();

        Website saved = websiteRepository.save(website);
        triggerCrawl(saved.getId(), saved.getDomain());
        return mapToResponse(saved);
    }

    public void triggerCrawl(Long websiteId, String domain) {
        new Thread(() -> {
            try {
                // Determine the correct URL to scrape (Add https if missing)
                String targetUrl = domain;
                if (!targetUrl.startsWith("http")) {
                    targetUrl = "https://" + domain; 
                }
                
                // 1. Scrape using native Java JSoup Scraper
                String extractedText = webCrawlerService.scrapeUrl(targetUrl);
                
                // 2. Save directly to database
                scrapedContentService.saveScrapedContent(websiteId, targetUrl, extractedText);
                
                // 3. Update last crawled timestamp
                Website website = websiteRepository.findById(websiteId).orElse(null);
                if (website != null) {
                    website.setLastCrawledAt(java.time.LocalDateTime.now());
                    websiteRepository.save(website);
                }
                
            } catch (Exception e) {
                System.err.println("Scraping failed for website " + websiteId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public List<WebsiteResponse> getAllWebsites() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email).orElseThrow();
        
        return websiteRepository.findAllByOwner(owner).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WebsiteResponse getByToken(String token) {
        return websiteRepository.findByBotToken(token)
                .map(this::mapToResponse)
                .orElseThrow();
    }

    private WebsiteResponse mapToResponse(Website website) {
        return WebsiteResponse.builder()
                .id(website.getId())
                .domain(website.getDomain())
                .name(website.getName())
                .botName(website.getBotName())
                .welcomeMessage(website.getWelcomeMessage())
                .primaryColor(website.getPrimaryColor())
                .botToken(website.getBotToken())
                .active(website.isActive())
                .lastCrawledAt(website.getLastCrawledAt())
                .build();
    }
}
