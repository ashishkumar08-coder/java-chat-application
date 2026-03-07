package com.college.chat.controller;

import com.college.chat.model.ChatMessage;
import com.college.chat.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private AssistantService assistant;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        String content = chatMessage.getContent();

        if (content == null) return chatMessage;

        // COMMAND 1: @bot (General AI)
        if (content.trim().toLowerCase().startsWith("@bot")) {
            String prompt = content.replaceFirst("(?i)@bot", "").trim();
            String response = assistant.getAIResponse(prompt);
            return createBotMessage(response);
        }

        // COMMAND 2: @translate (e.g., @translate Spanish Hello world)
        if (content.trim().toLowerCase().startsWith("@translate")) {
            String parts = content.replaceFirst("(?i)@translate", "").trim();
            String[] splitParts = parts.split(" ", 2);
            
            if (splitParts.length < 2) {
                return createBotMessage("Usage: @translate [language] [text]");
            }
            
            String response = assistant.translateText(splitParts[1], splitParts[0]);
            return createBotMessage(response);
        }

        return chatMessage;
    }

    private ChatMessage createBotMessage(String text) {
        ChatMessage botMessage = new ChatMessage();
        botMessage.setSender("AI Assistant");
        botMessage.setContent(text);
        botMessage.setType(ChatMessage.MessageType.CHAT);
        return botMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
}
