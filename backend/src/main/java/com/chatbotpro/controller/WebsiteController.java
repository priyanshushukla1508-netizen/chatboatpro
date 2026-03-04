package com.chatbotpro.controller;

import com.chatbotpro.dto.website.WebsiteRequest;
import com.chatbotpro.dto.website.WebsiteResponse;
import com.chatbotpro.service.WebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/websites")
@RequiredArgsConstructor
public class WebsiteController {

    private final WebsiteService websiteService;

    @PostMapping
    public ResponseEntity<WebsiteResponse> createWebsite(
            @RequestBody WebsiteRequest request
    ) {
        return ResponseEntity.ok(websiteService.createWebsite(request));
    }

    @GetMapping
    public ResponseEntity<List<WebsiteResponse>> getAllWebsites() {
        return ResponseEntity.ok(websiteService.getAllWebsites());
    }

    @PostMapping("/{id}/crawl")
    public ResponseEntity<Void> triggerCrawl(@PathVariable Long id) {
        WebsiteResponse website = websiteService.getAllWebsites().stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElseThrow();
        websiteService.triggerCrawl(id, website.getDomain());
        return ResponseEntity.accepted().build();
    }
}
