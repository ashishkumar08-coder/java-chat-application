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

        // 1. Check if the message is for the AI (starts with @bot)
        if (content != null && content.trim().toLowerCase().startsWith("@bot")) {
            // Remove the "@bot" trigger from the prompt
            String prompt = content.replaceFirst("(?i)@bot", "").trim();
            
            // 2. Call the AI service (this matches the getAIResponse method)
            String aiResponse = assistant.getAIResponse(prompt);
            
            // 3. Return a new message specifically from the AI Assistant
            ChatMessage botMessage = new ChatMessage();
            botMessage.setSender("AI Assistant");
            botMessage.setContent(aiResponse);
            botMessage.setType(ChatMessage.MessageType.CHAT);
            return botMessage;
        }

        // If not a bot command, just pass the original message through
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
}
