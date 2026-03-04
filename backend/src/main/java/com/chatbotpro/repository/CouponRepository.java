package com.chatbotpro.repository;

import com.chatbotpro.entity.Coupon;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByWebsiteAndActiveTrue(Website website);
}
