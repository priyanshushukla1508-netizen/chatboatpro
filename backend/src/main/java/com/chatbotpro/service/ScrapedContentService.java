package com.chatbotpro.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapedContentService {

    @Value("${crawler.output.path}")
    private String crawlerOutputPath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getContentForWebsite(Long websiteId) {
        File file = new File(crawlerOutputPath + "/website_" + websiteId + ".json");
        if (!file.exists()) {
            return "";
        }

        try {
            List<Map<String, Object>> items = objectMapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
            return items.stream()
                    .map(item -> (String) item.get("content"))
                    .filter(content -> content != null && !content.isEmpty())
                    .collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            return "";
        }
    }
}
