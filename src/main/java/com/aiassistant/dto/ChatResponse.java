package com.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ChatResponse {
    private String reply;
    
    public ChatResponse() {
    	
    }
    
    public ChatResponse(String name) {
    	this.reply = name;
    }

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}
}
