package com.chatbotpro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRM Integration Service — Pushes captured leads to Zoho CRM or HubSpot.
 * Configure via application.properties:
 *   crm.provider=zoho  (or hubspot)
 *   crm.zoho.api.key=your_key
 *   crm.hubspot.api.key=your_key
 */
@Service
@RequiredArgsConstructor
public class CRMService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${crm.provider:none}")
    private String crmProvider;

    @Value("${crm.zoho.api.key:}")
    private String zohoApiKey;

    @Value("${crm.hubspot.api.key:}")
    private String hubspotApiKey;

    /**
     * Push a lead to the configured CRM.
     * Called automatically when visitor provides name/email in chat.
     */
    public void pushLead(String name, String email, String phone, String websiteName) {
        try {
            if ("zoho".equalsIgnoreCase(crmProvider) && !zohoApiKey.isBlank()) {
                pushToZoho(name, email, phone, websiteName);
            } else if ("hubspot".equalsIgnoreCase(crmProvider) && !hubspotApiKey.isBlank()) {
                pushToHubSpot(name, email, phone, websiteName);
            }
            // If no CRM configured, silently skip
        } catch (Exception e) {
            System.err.println("CRM push failed: " + e.getMessage());
        }
    }

    private void pushToZoho(String name, String email, String phone, String source) {
        String url = "https://www.zohoapis.in/crm/v2/Leads";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Zoho-oauthtoken " + zohoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> lead = new HashMap<>();
        lead.put("Last_Name", name);
        lead.put("Email", email);
        lead.put("Phone", phone);
        lead.put("Lead_Source", "ChatBotPro Widget — " + source);

        Map<String, Object> body = new HashMap<>();
        body.put("data", List.of(lead));

        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }

    private void pushToHubSpot(String name, String email, String phone, String source) {
        String url = "https://api.hubapi.com/crm/v3/objects/contacts?hapikey=" + hubspotApiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> properties = new HashMap<>();
        properties.put("email", email);
        properties.put("firstname", name);
        properties.put("phone", phone);
        properties.put("lead_source", "ChatBotPro — " + source);

        Map<String, Object> body = new HashMap<>();
        body.put("properties", properties);

        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }
}
