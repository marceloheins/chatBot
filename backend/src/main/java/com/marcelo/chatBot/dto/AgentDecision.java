package com.marcelo.chatBot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentDecision {
    private String agente;
    private String decisao;
    
}
