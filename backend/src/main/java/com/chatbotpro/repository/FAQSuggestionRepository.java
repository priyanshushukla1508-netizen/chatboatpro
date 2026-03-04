package com.chatbotpro.repository;

import com.chatbotpro.entity.FAQSuggestion;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQSuggestionRepository extends JpaRepository<FAQSuggestion, Long> {
    List<FAQSuggestion> findAllByWebsiteAndStatus(Website website, FAQSuggestion.SuggestionStatus status);
}
