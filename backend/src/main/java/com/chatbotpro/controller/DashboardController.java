package com.chatbotpro.controller;

import com.chatbotpro.dto.dashboard.DashboardResponse;
import com.chatbotpro.entity.ChatMessage;
import com.chatbotpro.entity.KnowledgeGap;
import com.chatbotpro.service.DashboardService;
import com.chatbotpro.service.KnowledgeGapService;
import com.chatbotpro.service.ChatService;
import com.chatbotpro.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final KnowledgeGapService knowledgeGapService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    // === LIVE INBOX ===
    @GetMapping("/sessions/{websiteId}")
    public ResponseEntity<List<Map<String, Object>>> getWebSessions(@PathVariable Long websiteId) {
        List<String> sessionIds = chatMessageRepository.findUniqueSessionsByWebsiteId(websiteId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (String sid : sessionIds) {
            boolean needsHuman = false;
            List<ChatMessage> msgs = chatMessageRepository.findAllBySessionIdOrderByCreatedAtAsc(sid);
            if (!msgs.isEmpty()) {
                // Look at the last 3 messages to see if human intervention was requested or suggested
                int start = Math.max(0, msgs.size() - 3);
                for (int i = start; i < msgs.size(); i++) {
                    String text = msgs.get(i).getMessage().toLowerCase();
                    if (text.contains("human") || text.contains("agent") || text.contains("real person")) {
                        needsHuman = true;
                        break;
                    }
                }
            }
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("sessionId", sid);
            map.put("needsHuman", needsHuman);
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chat/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatMessageRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId));
    }

    @PostMapping("/chat/send")
    public ResponseEntity<ChatMessage> sendAdminMessage(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String message = payload.get("message");
        Long websiteId = Long.parseLong(payload.get("websiteId"));
        return ResponseEntity.ok(dashboardService.sendAdminResponse(websiteId, sessionId, message));
    }

    @PostMapping("/chat/note")
    public ResponseEntity<ChatMessage> sendInternalNote(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String note = payload.get("note");
        Long websiteId = Long.parseLong(payload.get("websiteId"));
        return ResponseEntity.ok(dashboardService.sendInternalNote(websiteId, sessionId, note));
    }

    // === KNOWLEDGE GAPS ===
    @GetMapping("/gaps/{websiteId}")
    public ResponseEntity<List<KnowledgeGap>> getGaps(@PathVariable Long websiteId) {
        return ResponseEntity.ok(knowledgeGapService.getUnresolvedGaps(websiteId));
    }

    @PostMapping("/gaps/{gapId}/resolve")
    public ResponseEntity<Void> resolveGap(@PathVariable Long gapId, @RequestBody Map<String, String> body) {
        knowledgeGapService.resolveGap(gapId, body.get("answer"));
        return ResponseEntity.ok().build();
    }
}
