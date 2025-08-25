package com.marcelo.chatBot.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatResponse {
    private String resposta;
    private String fontAgentResposta;
    private List<AgentDecision> agenteFluxoTrabalho;
    
}
