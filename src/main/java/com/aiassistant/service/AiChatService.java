package com.aiassistant.service;

import com.aiassistant.entity.HRPolicy;
import com.aiassistant.repository.HRPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final ChatClient chatClient;
    private final HRPolicyRepository hrPolicyRepository;
    
    public AiChatService(ChatClient client , HRPolicyRepository repo) {
    	this.chatClient = client;
    	this.hrPolicyRepository = repo;
    }

    private static final String SYSTEM_PROMPT = """
            You are an AI HR Support Assistant for company employees.
            Answer questions about HR policy, leave, attendance and payroll
            clearly and concisely, using the provided company policy context
            when relevant. If you don't know the answer, say so and suggest
            the employee contact HR directly. Do not make up policy details.

            Multi-language support: always reply in the same language the
            employee used in their message (for example, if they write in
            Tamil, English, Hindi, or a mix of languages, reply naturally in
            that same language or mix). Do not switch to English unless the
            employee wrote in English.
            """;

    public String getReply(String userMessage) {
        String context = buildContext();

        String fullSystemPrompt = SYSTEM_PROMPT + "\n\nCompany policy context:\n" + context;

        Prompt prompt = new Prompt(List.of(
                new SystemMessage(fullSystemPrompt),
                new UserMessage(userMessage)
        ));

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    private String buildContext() {
        List<HRPolicy> policies = hrPolicyRepository.findAll();
        if (policies.isEmpty()) {
            return "No policy documents are currently loaded.";
        }
        return policies.stream()
                .map(p -> "- " + p.getTitle() + ": " + p.getContent())
                .collect(Collectors.joining("\n"));
    }
}