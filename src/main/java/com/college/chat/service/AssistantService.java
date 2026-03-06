package com.college.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssistantService {

    public String processAIRequest(String userPrompt) {
        String query = userPrompt.toLowerCase();
        
        if (query.contains("who are you")) return "I am your AI Chat Assistant, built for your college project!";
        if (query.contains("time")) return "The current server time is " + java.time.LocalTime.now();
        if (query.contains("help")) return "I can help you translate messages or answer basic questions. Use @bot [message]";
        
        return "I'm still learning! But I received your prompt: " + userPrompt;
    }

    // A simulated translation feature (Professional apps use LibreTranslate or Google Free Tier)
    public String translateText(String text, String targetLang) {
        // For a college project, this demonstrates the 'Feature'
        // In a live version, you'd call a translation API here
        return " [Translated to " + targetLang + "]: " + text;
    }
}