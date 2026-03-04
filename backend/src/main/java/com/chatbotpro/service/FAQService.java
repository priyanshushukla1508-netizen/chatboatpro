package com.chatbotpro.service;

import com.chatbotpro.dto.faq.FAQRequest;
import com.chatbotpro.dto.faq.FAQResponse;
import com.chatbotpro.entity.FAQ;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.FAQRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;
    private final WebsiteRepository websiteRepository;
    private final com.chatbotpro.client.GroqClient groqClient;

    public List<FAQResponse> getFAQsByWebsite(Long websiteId) {
        Website website = websiteRepository.findById(websiteId).orElseThrow();
        return faqRepository.findAllByWebsite(website).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FAQResponse createFAQ(FAQRequest request) {
        Website website = websiteRepository.findById(request.getWebsiteId()).orElseThrow();
        FAQ faq = FAQ.builder()
                .website(website)
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .isAIGenerated(false)
                .build();
        FAQ saved = faqRepository.save(faq);
        return mapToResponse(saved);
    }

    public void deleteFAQ(Long id) {
        faqRepository.deleteById(id);
    }

    private FAQResponse mapToResponse(FAQ faq) {
        return FAQResponse.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .isAIGenerated(faq.isAIGenerated())
                .createdAt(faq.getCreatedAt())
                .build();
    }

    /**
     * Extracts text from an uploaded document (txt/pdf) and uses AI to parse it into FAQs.
     */
    public int uploadDocument(Long websiteId, org.springframework.web.multipart.MultipartFile file) {
        Website website = websiteRepository.findById(websiteId).orElseThrow();
        String content = "";

        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null) throw new RuntimeException("Invalid file");

            if (fileName.endsWith(".txt")) {
                content = new String(file.getBytes());
            } else if (fileName.endsWith(".pdf")) {
                try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(file.getBytes())) {
                    org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                    content = stripper.getText(document);
                }
            } else {
                throw new RuntimeException("Unsupported file format. Please upload .txt or .pdf");
            }

            if (content.trim().isEmpty()) throw new RuntimeException("File is empty");

            // Use Groq AI to extract FAQs from the text
            String systemPrompt = "You are an expert AI Training agent. Extract all possible Question-Answer pairs from the provided text. " +
                    "Return the response ONLY as a JSON array of objects with 'question' and 'answer' fields. " +
                    "Example: [{\"question\": \"...\", \"answer\": \"...\"}]";
            
            // Reusing AutoFAQService's Groq logic but manually calling it here for simplicity
            // In a larger system, this would be moved to a shared AI parsing service
            String aiResponse = groqClient.getCompletion(systemPrompt, "Document content:\n" + content);
            
            // Clean AI response
            String cleanedJson = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<java.util.Map<String, String>> extractedFaqs = mapper.readValue(cleanedJson, new com.fasterxml.jackson.core.type.TypeReference<List<java.util.Map<String, String>>>() {});

            int count = 0;
            for (java.util.Map<String, String> entry : extractedFaqs) {
                FAQ faq = FAQ.builder()
                        .website(website)
                        .question(entry.get("question"))
                        .answer(entry.get("answer"))
                        .isAIGenerated(true)
                        .build();
                faqRepository.save(faq);
                count++;
            }
            return count;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process document: " + e.getMessage());
        }
    }
}
