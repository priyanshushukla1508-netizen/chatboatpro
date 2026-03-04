package com.chatbotpro.repository;

import com.chatbotpro.entity.User;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WebsiteRepository extends JpaRepository<Website, Long> {
    Optional<Website> findByBotToken(String botToken);
    List<Website> findAllByOwner(User owner);
    long countByOwner(User owner);
}
