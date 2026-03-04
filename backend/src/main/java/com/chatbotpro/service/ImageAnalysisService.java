package com.chatbotpro.service;

import com.chatbotpro.client.GroqClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * GPT-Vision: Analyzes customer-uploaded images using Groq's vision model (llama-3.2-11b-vision).
 * Customer sends a photo of damaged product, error screen, etc.
 * AI sees the image and replies contextually.
 */
@Service
@RequiredArgsConstructor
public class ImageAnalysisService {

    private final GroqClient groqClient;

    public String analyzeImage(MultipartFile imageFile, String userContext) {
        try {
            // Convert image to Base64
            byte[] bytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            String mimeType = imageFile.getContentType();

            // Build vision prompt
            String systemPrompt = """
                    You are a helpful customer support AI that analyzes images sent by customers.
                    Look at the image carefully and provide a helpful, empathetic response.
                    If it's a damaged product, acknowledge the issue and explain next steps.
                    If it's an error screenshot, explain what the error means and how to fix it.
                    Respond in the same language the customer used (Hindi/Hinglish/English).
                    Keep responses concise (max 3-4 sentences).
                    """;

            String userPrompt = userContext != null && !userContext.isBlank()
                    ? userContext
                    : "Please analyze this image and help me with my query.";

            // Call Groq Vision API
            return groqClient.callVision(systemPrompt, userPrompt, base64Image, mimeType);

        } catch (Exception e) {
            return "Image analysis failed. Please describe your issue in text and I'll help you right away!";
        }
    }
}
