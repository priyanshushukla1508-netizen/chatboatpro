package com.chatbotpro.controller;

import com.chatbotpro.dto.faq.FAQRequest;
import com.chatbotpro.dto.faq.FAQResponse;
import com.chatbotpro.service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faqs")
@RequiredArgsConstructor
public class FAQController {

    private final FAQService faqService;

    @GetMapping("/website/{websiteId}")
    public ResponseEntity<List<FAQResponse>> getFAQsByWebsite(
            @PathVariable Long websiteId
    ) {
        return ResponseEntity.ok(faqService.getFAQsByWebsite(websiteId));
    }

    @PostMapping
    public ResponseEntity<FAQResponse> createFAQ(
            @RequestBody FAQRequest request
    ) {
        return ResponseEntity.ok(faqService.createFAQ(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload/{websiteId}")
    public ResponseEntity<Integer> uploadDocument(
            @PathVariable Long websiteId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file
    ) {
        return ResponseEntity.ok(faqService.uploadDocument(websiteId, file));
    }
}
