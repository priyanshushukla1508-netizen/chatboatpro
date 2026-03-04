package com.chatbotpro.service;

import com.chatbotpro.entity.Coupon;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.CouponRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final WebsiteRepository websiteRepository;

    /**
     * Called by widget when exit-intent is detected.
     * Returns the active coupon for the website, if any.
     */
    public Optional<Coupon> getActiveCoupon(String botToken) {
        Website website = websiteRepository.findByBotToken(botToken)
                .orElse(null);
        if (website == null) return Optional.empty();
        return couponRepository.findByWebsiteAndActiveTrue(website);
    }

    /**
     * Owner creates/updates coupon from dashboard.
     */
    public Coupon saveCoupon(Long websiteId, String code, int discountPercent, String message, int timerSeconds) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));

        // Deactivate existing coupons for this website
        couponRepository.findByWebsiteAndActiveTrue(website)
                .ifPresent(c -> { c.setActive(false); couponRepository.save(c); });

        Coupon coupon = Coupon.builder()
                .website(website)
                .code(code.toUpperCase())
                .discountPercent(discountPercent)
                .message(message)
                .timerSeconds(timerSeconds)
                .active(true)
                .build();
        return couponRepository.save(coupon);
    }
}
