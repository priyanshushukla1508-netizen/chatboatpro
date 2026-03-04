package com.chatbotpro.repository;

import com.chatbotpro.entity.Product;
import com.chatbotpro.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByWebsite(Website website);
}
