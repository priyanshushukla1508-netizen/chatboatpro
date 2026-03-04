package com.chatbotpro.controller;

import com.chatbotpro.entity.Coupon;
import com.chatbotpro.entity.CSATRating;
import com.chatbotpro.entity.Product;
import com.chatbotpro.service.CouponService;
import com.chatbotpro.service.CSATService;
import com.chatbotpro.service.ProductRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Widget-facing APIs (no auth required, public endpoint).
 * All prefixed with /api/v1/widget
 */
@RestController
@RequestMapping("/api/v1/widget")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WidgetFeaturesController {

    private final CouponService couponService;
    private final CSATService csatService;
    private final ProductRecommendationService productService;

    // === COUPON ENGINE ===
    @GetMapping("/coupon/{botToken}")
    public ResponseEntity<?> getCoupon(@PathVariable String botToken) {
        Optional<Coupon> coupon = couponService.getActiveCoupon(botToken);
        return coupon.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // === CSAT RATING ===
    @PostMapping("/csat")
    public ResponseEntity<CSATRating> submitRating(@RequestBody Map<String, Object> body) {
        String botToken = (String) body.get("botToken");
        String sessionId = (String) body.get("sessionId");
        int rating = Integer.parseInt(body.get("rating").toString());
        String feedback = (String) body.getOrDefault("feedback", "");
        return ResponseEntity.ok(csatService.submitRating(botToken, sessionId, rating, feedback));
    }

    // === PRODUCT RECOMMENDATIONS ===
    @GetMapping("/products/{botToken}")
    public ResponseEntity<List<Product>> recommendProducts(
            @PathVariable String botToken,
            @RequestParam String query) {
        return ResponseEntity.ok(productService.recommend(botToken, query));
    }
}
