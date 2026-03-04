package com.chatbotpro.controller;

import com.chatbotpro.entity.FAQSuggestion;
import com.chatbotpro.service.AutoFAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faqs/suggestions")
@RequiredArgsConstructor
public class AutoFAQController {

    private final AutoFAQService autoFAQService;

    @GetMapping("/{websiteId}")
    public ResponseEntity<List<FAQSuggestion>> getSuggestions(@PathVariable Long websiteId) {
        return ResponseEntity.ok(autoFAQService.getPendingSuggestions(websiteId));
    }

    @PostMapping("/{websiteId}/scan")
    public ResponseEntity<Void> scanChatHistory(@PathVariable Long websiteId) {
        autoFAQService.generateSuggestions(websiteId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveSuggestion(@PathVariable Long id) {
        autoFAQService.approveSuggestion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectSuggestion(@PathVariable Long id) {
        autoFAQService.rejectSuggestion(id);
        return ResponseEntity.ok().build();
    }
}
