package com.chatbotpro.repository;

import com.chatbotpro.entity.ChatSession;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByWebsiteAndSessionId(Website website, String sessionId);
}
