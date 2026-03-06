package com.college.chat.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssistantService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String processAIRequest(String userPrompt) {
        String query = userPrompt.toLowerCase().trim();
        if (query.contains("help")) return "I am your AI. Use @bot [question] or 'Translate to [Language]: [Text]'.";
        if (query.contains("who made you")) return "I was built for a professional college project!";
        return "🤖 AI Assistant: I have analyzed your message: '" + userPrompt + "'. How else can I help?";
    }

    public String translateText(String content) {
        try {
            // Logic: "Translate to Spanish: Hello"
            String[] parts = content.split(":");
            String targetLang = parts[0].toLowerCase().replace("translate to", "").trim();
            String textToTranslate = parts[1].trim();

            String url = "https://api.mymemory.translated.net/get?q=" + textToTranslate + "&langpair=en|" + targetLang;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> responseData = (Map<String, Object>) response.get("responseData");
            return "🌍 [" + targetLang.toUpperCase() + "]: " + responseData.get("translatedText").toString();
        } catch (Exception e) {
            return "⚠️ Translation Error: Please use format 'Translate to [Lang]: [Text]'";
        }
    }
}