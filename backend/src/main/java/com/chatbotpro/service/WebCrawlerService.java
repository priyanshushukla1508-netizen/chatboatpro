package com.chatbotpro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCrawlerService {

    /**
     * Scrapes a website URL directly from Java using JSoup.
     * Replaces the old Python FastAPI crawler.
     */
    public String scrapeUrl(String url) {
        try {
            log.info("Starting native Java crawl for URL: {}", url);
            
            // Connect to the URL and fetch the HTML document
            // Added realistic user-agent to avoid basic bot blockers
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(30000)
                    .get();

            // Strip out non-content elements to save tokens
            doc.select("script, style, iframe, nav, footer, header").remove();

            // Extract the clean text
            String text = doc.text();
            
            // Basic cleaning: remove excessive whitespace
            text = text.replaceAll("\\s{2,}", " ").trim();
            
            // Limit to roughly 15,000 characters to prevent Groq context window overflow
            if (text.length() > 15000) {
                text = text.substring(0, 15000) + "... [Content Truncated]";
            }
            
            log.info("Successfully scraped {} characters from {}", text.length(), url);
            return text;
            
        } catch (IOException e) {
            log.error("Failed to scrape URL with JSoup: {}", url, e);
            throw new RuntimeException("Crawling failed for URL: " + url + " - " + e.getMessage());
        }
    }
}
