package com.chatbotpro.service;

import com.chatbotpro.entity.Product;
import com.chatbotpro.entity.Website;
import com.chatbotpro.repository.ProductRepository;
import com.chatbotpro.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final ProductRepository productRepository;
    private final WebsiteRepository websiteRepository;

    /**
     * Match user query keywords against product keywords.
     * Returns up to 3 best matching products.
     */
    public List<Product> recommend(String botToken, String userQuery) {
        Website website = websiteRepository.findByBotToken(botToken).orElse(null);
        if (website == null) return Collections.emptyList();

        List<Product> allProducts = productRepository.findAllByWebsite(website);
        String query = userQuery.toLowerCase();

        return allProducts.stream()
                .filter(p -> {
                    // Check if any keyword matches user query
                    String[] keywords = p.getKeywords() != null
                            ? p.getKeywords().toLowerCase().split(",")
                            : new String[]{};
                    return Arrays.stream(keywords)
                            .anyMatch(kw -> query.contains(kw.trim())
                                    || p.getName().toLowerCase().contains(kw.trim()));
                })
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts(Long websiteId) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));
        return productRepository.findAllByWebsite(website);
    }

    public Product saveProduct(Long websiteId, String name, String description,
                               String imageUrl, Double price, String buyLink, String keywords) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));
        return productRepository.save(Product.builder()
                .website(website).name(name).description(description)
                .imageUrl(imageUrl).price(price).buyLink(buyLink).keywords(keywords).build());
    }
}
