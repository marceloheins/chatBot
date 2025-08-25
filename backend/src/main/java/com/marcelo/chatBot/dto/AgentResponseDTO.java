package com.marcelo.chatBot.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentResponseDTO{
    private String resposta;
    private String fontAgentResposta;
    private long executionTimeMs;
    private List<AgentDecision> AgentWorkflow;
}