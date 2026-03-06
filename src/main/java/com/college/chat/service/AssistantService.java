package com.college.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssistantService {

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // ============================================================
    // 🛑 PASTE YOUR API KEY IN THE QUOTES BELOW 🛑
    private final String API_KEY = "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"; 
    // ============================================================

    public String processAIRequest(String userPrompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.set("HTTP-Referer", "http://localhost:8080"); // Required for OpenRouter

            Map<String, Object> requestBody = new HashMap<>();
            // Using the best FREE model available
            requestBody.put("model", "google/gemini-2.0-flash-lite-preview-02-05:free");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", userPrompt));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            // Extracting the text from the AI's JSON response
            List choices = (List) response.getBody().get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");
            
            return message.get("content").toString();

        } catch (Exception e) {
            return "🤖 AI Assistant Error: " + e.getMessage();
        }
    }

    public String translateText(String content) {
        // This sends a special command to the AI to act as a translator
        String prompt = "Translate the following text accurately. Return ONLY the translated result: " + content;
        return processAIRequest(prompt);
    }
}