package com.chatbotpro.repository;

import com.chatbotpro.entity.ChatMessage;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllBySessionIdOrderByCreatedAtAsc(String sessionId);
    List<ChatMessage> findAllByWebsite(Website website);
    long countByWebsiteAndSenderType(Website website, String senderType);

    @Query("SELECT DISTINCT m.sessionId FROM ChatMessage m WHERE m.website.id = :websiteId ORDER BY m.sessionId DESC")
    List<String> findUniqueSessionsByWebsiteId(@Param("websiteId") Long websiteId);

    List<ChatMessage> findAllByWebsiteIdOrderByCreatedAtDesc(Long websiteId);
}
