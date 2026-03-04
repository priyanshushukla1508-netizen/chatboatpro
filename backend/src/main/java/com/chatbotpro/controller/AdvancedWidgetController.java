package com.chatbotpro.controller;

import com.chatbotpro.entity.LeadScore;
import com.chatbotpro.service.ImageAnalysisService;
import com.chatbotpro.service.LeadScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/widget")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdvancedWidgetController {

    private final ImageAnalysisService imageAnalysisService;
    private final LeadScoringService leadScoringService;

    // === FEATURE 1: GPT-VISION IMAGE ANALYSIS ===
    @PostMapping(value = "/analyze-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> analyzeImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "context", defaultValue = "") String context) {

        String analysis = imageAnalysisService.analyzeImage(image, context);
        return ResponseEntity.ok(Map.of("response", analysis));
    }

    // === FEATURE 2: LEAD SCORING — UPDATE BEHAVIOR ===
    @PostMapping("/lead-score")
    public ResponseEntity<LeadScore> updateLeadScore(@RequestBody Map<String, Object> payload) {
        String botToken = (String) payload.get("botToken");
        String sessionId = (String) payload.get("sessionId");

        @SuppressWarnings("unchecked")
        Map<String, Object> behavior = (Map<String, Object>) payload.get("behavior");

        LeadScore score = leadScoringService.updateScore(botToken, sessionId, behavior);
        return score != null ? ResponseEntity.ok(score) : ResponseEntity.badRequest().build();
    }

    // === FEATURE 3: CART RECOVERY EVENT LOG ===
    @PostMapping("/cart-recovery")
    public ResponseEntity<Map<String, String>> logCartRecovery(@RequestBody Map<String, Object> payload) {
        // Log cart recovery trigger — future: store in DB for analytics
        String botToken = (String) payload.get("botToken");
        String sessionId = (String) payload.get("sessionId");
        System.out.println("[CART RECOVERY] Bot: " + botToken + " | Session: " + sessionId);
        return ResponseEntity.ok(Map.of("status", "logged"));
    }
}
