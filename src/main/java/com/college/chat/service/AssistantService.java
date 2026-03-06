package com.college.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class AssistantService {

    // IMPORTANT: Make sure GROQ_API_KEY is set in your Render Environment Variables
    private final String API_KEY = System.getenv("GROQ_API_KEY");
    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getAIResponse(String userPrompt) {
        // Safety check for the API Key
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "AI Error: API Key is missing. Please set GROQ_API_KEY in Render Environment settings.";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // Request Body Setup
            Map<String, Object> requestBody = new HashMap<>();
            // Using a high-performance, stable Groq model
            requestBody.put("model", "llama-3.3-70b-versatile");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", userPrompt));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Sending the request to Groq
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parsing the nested JSON response from Groq/OpenAI format
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                Map<String, Object> firstChoice = (Map<String, Object>) choices.get(0).get("message");
                return (String) firstChoice.get("content");
            } else {
                return "AI Error: Received status code " + response.getStatusCode();
            }

        } catch (Exception e) {
            // This will capture 401 (Unauthorized), 404 (Model not found), etc.
            return "AI Error: " + e.getMessage();
        }
    }
}
