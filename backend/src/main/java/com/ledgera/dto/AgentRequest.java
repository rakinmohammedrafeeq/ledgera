package com.ledgera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Incoming request body for POST /api/ai/agent.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest {

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;

    /** Optional previous conversation history for multi-turn conversational tool execution */
    private List<ChatMessage> history;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
