package com.marcelo.chatBot.dto;

import com.marcelo.chatBot.model.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionResult {
    private Agent agent;
    private String reason;
}
