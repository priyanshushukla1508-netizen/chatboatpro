package com.chatbotpro.service;

import org.springframework.stereotype.Service;
import java.util.Set;

/**
 * Multilingual Auto-Detection Service
 *
 * Detects whether a visitor message is in:
 *  - HINDI (Devanagari script)
 *  - HINGLISH (Latin-script Hindi/Urdu words commonly used in India)
 *  - ENGLISH (default)
 *
 * This is used to instruct Groq AI to mirror the visitor's language in its reply.
 */
@Service
public class LanguageDetectionService {

    // Common Hinglish/Roman-Hindi words used by Indian users
    private static final Set<String> HINGLISH_WORDS = Set.of(
            "kya", "hai", "hoon", "tha", "thi", "the",
            "nahi", "nhi", "haan", "han", "ji", "aap",
            "mujhe", "tumhe", "humara", "mera", "tera",
            "batao", "bata", "karo", "karta", "chahiye",
            "milta", "milega", "lagta", "lagega",
            "bohot", "bahut", "thoda", "zyada", "kaafi",
            "please", "kar", "se", "mein", "pe", "par",
            "yeh", "toh", "bhi", "agar", "kyun", "kaise",
            "kab", "kaun", "kahan", "kitna", "kuch",
            "accha", "acha", "thik", "thk", "sahi",
            "abhi", "baad", "pehle", "phir",
            "wala", "wali", "wale",
            "liye", "leke", "karke",
            "ek", "do", "teen", "char",
            "ho", "hoga", "hogi", "honge",
            "sochna", "dekhna", "jaana",
            "namaskar", "namaste", "shukriya", "dhanyawad"
    );

    public enum Language {
        HINDI,
        HINGLISH,
        ENGLISH
    }

    /**
     * Detects language of the given text.
     * Priority: Devanagari script (HINDI) > Hinglish keywords > ENGLISH
     */
    public Language detect(String text) {
        if (text == null || text.isBlank()) return Language.ENGLISH;

        // 1. Check for Devanagari Unicode characters (pure Hindi script)
        if (text.matches(".*[\\u0900-\\u097F]+.*")) {
            return Language.HINDI;
        }

        // 2. Check for Hinglish words in the lowercased message
        String lower = text.toLowerCase();
        String[] words = lower.split("[\\s,!?.]+");
        int hinglishCount = 0;
        for (String word : words) {
            if (HINGLISH_WORDS.contains(word)) {
                hinglishCount++;
            }
        }

        // If 1 or more Hinglish words detected, classify as HINGLISH
        if (hinglishCount >= 1) {
            return Language.HINGLISH;
        }

        return Language.ENGLISH;
    }

    /**
     * Returns a language instruction string to inject into the Groq system prompt.
     * This tells the AI to mirror the visitor's language.
     */
    public String getLanguageInstruction(Language language) {
        return switch (language) {
            case HINDI ->
                "IMPORTANT: The visitor is writing in Hindi (Devanagari). " +
                "You MUST reply ONLY in Hindi (Devanagari script). Do NOT mix English.";
            case HINGLISH ->
                "IMPORTANT: The visitor is writing in Hinglish (Roman-script Hindi/English mix). " +
                "You MUST reply in Hinglish — mix Hindi words written in English with friendly English. " +
                "Sound natural and Indian. Example: 'Bilkul! Aapke liye best plan Pro hai.'";
            case ENGLISH ->
                "IMPORTANT: The visitor is writing in English. " +
                "Reply in clear, friendly English only.";
        };
    }

    /**
     * Convenience: true if language is Hindi or Hinglish (used by ObjectionHandlerService)
     */
    public boolean isHindi(Language language) {
        return language == Language.HINDI || language == Language.HINGLISH;
    }
}
