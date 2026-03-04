package com.chatbotpro.controller;

import com.chatbotpro.dto.chat.ChatRequest;
import com.chatbotpro.dto.chat.ChatResponse;
import com.chatbotpro.dto.website.WebsiteResponse;
import com.chatbotpro.service.ChatService;
import com.chatbotpro.service.WebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/widget")
@RequiredArgsConstructor
public class WidgetController {

    private final WebsiteService websiteService;
    private final ChatService chatService;

    @GetMapping("/config/{token}")
    public ResponseEntity<WebsiteResponse> getConfig(@PathVariable String token) {
        return ResponseEntity.ok(websiteService.getByToken(token));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.getChatResponse(request));
    }

    @GetMapping("/chat/{sessionId}")
    public ResponseEntity<java.util.List<com.chatbotpro.entity.ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        java.util.List<com.chatbotpro.entity.ChatMessage> msgs = chatService.getChatHistoryForWidget(sessionId);
        return ResponseEntity.ok(msgs);
    }
}
