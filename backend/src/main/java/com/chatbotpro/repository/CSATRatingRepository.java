package com.chatbotpro.repository;

import com.chatbotpro.entity.CSATRating;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CSATRatingRepository extends JpaRepository<CSATRating, Long> {
    List<CSATRating> findAllByWebsite(Website website);
    @Query("SELECT AVG(r.rating) FROM CSATRating r WHERE r.website = ?1")
    Double findAverageRatingByWebsite(Website website);
}
