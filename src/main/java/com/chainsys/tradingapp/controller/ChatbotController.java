package com.chainsys.tradingapp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.chainsys.tradingapp.service.ChatbotService;

@Controller
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public String chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        return chatbotService.getChatbotResponse(userMessage);
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }
}
