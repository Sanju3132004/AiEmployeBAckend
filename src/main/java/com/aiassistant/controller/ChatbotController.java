package com.aiassistant.controller;

import com.aiassistant.dto.ChatRequest;
import com.aiassistant.dto.ChatResponse;
import com.aiassistant.service.AiChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "AI Chatbot")
public class ChatbotController {

    private final AiChatService aiChatService;
    
    public ChatbotController(AiChatService service) {
    	this.aiChatService = service;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@Valid @RequestBody ChatRequest request) {
        String reply = aiChatService.getReply(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
