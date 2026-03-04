package com.chatbotpro.repository;

import com.chatbotpro.entity.KnowledgeGap;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface KnowledgeGapRepository extends JpaRepository<KnowledgeGap, Long> {
    List<KnowledgeGap> findAllByWebsiteAndResolvedFalseOrderByOccurrenceCountDesc(Website website);
    Optional<KnowledgeGap> findByWebsiteAndQuestion(Website website, String question);
}
