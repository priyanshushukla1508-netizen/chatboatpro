package com.chatbotpro.repository;

import com.chatbotpro.entity.FAQ;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findAllByWebsite(Website website);
}
