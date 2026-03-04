package com.chatbotpro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Auto-Objection Handler — detects sales objections and responds with
 * psychologically optimized counter-arguments.
 *
 * Based on SPIN Selling + Challenger Sale methodology.
 * Works inline with ChatService — no extra API call needed.
 */
@Service
@RequiredArgsConstructor
public class ObjectionHandlerService {

    // Objection keyword groups
    private static final List<String> PRICE_OBJECTIONS = List.of(
            "too expensive", "bahut expensive", "mehnga", "bohot mehenga",
            "costly", "budget nahi", "afford nahi", "paisa nahi", "thoda mahanga",
            "price kam karo", "discount", "kitna discount"
    );

    private static final List<String> HESITATION_OBJECTIONS = List.of(
            "sochna hai", "i'll think", "will think", "soch ke batata", "soch ke batati",
            "not sure", "sure nahi", "kaafi sochna padega", "abhi nahi", "baad mein",
            "later", "next month", "agli baar"
    );

    private static final List<String> TRUST_OBJECTIONS = List.of(
            "trust nahi", "reliable hai", "kya guarantee", "kya bharosa",
            "is it legit", "real hai", "fake toh nahi", "verified",
            "reviews kya hain", "testimonials"
    );

    private static final List<String> COMPETITOR_OBJECTIONS = List.of(
            "intercom", "tidio", "drift", "freshchat", "zoho desk",
            "ye bhi toh hai", "ye wala better", "doosra product"
    );

    /**
     * Detects if a user message contains a known objection pattern.
     * Returns a sales-psychology optimized response if detected, else null.
     */
    public String handleObjection(String userMessage, boolean isHindi) {
        String msg = userMessage.toLowerCase();

        // PRICE OBJECTION
        if (matchesAny(msg, PRICE_OBJECTIONS)) {
            return isHindi
                    ? "Main samajh sakta hoon! Lekin sochen — agar ChatBotPro sirf **5 extra leads** per month convert kare, toh ₹19,999 toh pehle hafte mein recover ho jata hai. Hamare clients ka average ROI 340% hai pehle 60 dino mein. Kya aap ek free 2-din trial se shuru karna chahenge?"
                    : "I completely understand! But consider this — if ChatBotPro converts just **5 extra leads per month**, ₹19,999 pays for itself within the first week. Our clients see an average ROI of 340% within 60 days. Want to start with a free 2-day trial and see the results yourself?";
        }

        // HESITATION OBJECTION
        if (matchesAny(msg, HESITATION_OBJECTIONS)) {
            return isHindi
                    ? "Bilkul sochein! Lekin yeh bhi sochein — har din aap bina bot ke customers ko miss kar rahe hain jo raat ko 2 baje query karte hain. Sirf 2 din ka FREE trial hai — koi commitment nahi, koi credit card nahi. Sirf results dekhein. Shuru karein?"
                    : "Of course, take your time! But here's something to consider — every day without a bot, you're missing visitors who query at 2am. Our 2-day FREE trial has zero commitment and no credit card. Just see the results. Want to start now?";
        }

        // TRUST OBJECTION
        if (matchesAny(msg, TRUST_OBJECTIONS)) {
            return isHindi
                    ? "Bahut valid concern hai! Isliye hum pehle 2-din FREE trial dete hain — bina payment ke. Hamare 200+ clients mein se (Jaipur, Mumbai, Delhi) sab ne pehle try kiya, phir kharida. Main aapko ek live client ka contact bhi de sakta hoon. Bharosa khud dekhiye!"
                    : "That's a completely valid concern! That's exactly why we offer a 2-day FREE trial — no payment needed. All 200+ of our clients (across Jaipur, Mumbai, Delhi) tried before they bought. I can even connect you with a live client reference. Judge for yourself!";
        }

        // COMPETITOR OBJECTION
        if (matchesAny(msg, COMPETITOR_OBJECTIONS)) {
            return isHindi
                    ? "Intercom/Tidio ki comparison mein ChatBotPro ke 3 fark hain:\n✅ Native Hinglish support (unke paas nahi)\n✅ Price 10x kam (₹19,999 vs $500+)\n✅ Website auto-crawl — manual training zero\n\nAur sabse badi baat — woh generic hain, hum Indian market ke liye bane hain."
                    : "Great that you're comparing! ChatBotPro has 3 key advantages:\n✅ Native Hinglish support (they don't have this)\n✅ 10x cheaper pricing (₹19,999 vs $500+)\n✅ Auto website crawl — zero manual training\n\nMost importantly — they're generic, we're built for India.";
        }

        return null; // No objection detected
    }

    private boolean matchesAny(String msg, List<String> patterns) {
        return patterns.stream().anyMatch(msg::contains);
    }
}
