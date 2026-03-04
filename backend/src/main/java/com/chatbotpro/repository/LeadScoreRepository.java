package com.chatbotpro.repository;

import com.chatbotpro.entity.LeadScore;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeadScoreRepository extends JpaRepository<LeadScore, Long> {
    Optional<LeadScore> findByWebsiteAndSessionId(Website website, String sessionId);
    List<LeadScore> findAllByWebsiteOrderByScoreDesc(Website website);
    List<LeadScore> findAllByWebsiteAndTier(Website website, String tier);
}
