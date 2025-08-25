package com.marcelo.chatBot;

import com.marcelo.chatBot.service.MathAgentService;
import com.marcelo.chatBot.dto.AgentResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MathAgentServiceTest {

    @Test
    void calculateSimpleExpression() {
        MathAgentService s = new MathAgentService();
        AgentResponseDTO dto = s.process("70 + 12", "conv-1", "user-1");
        assertTrue(dto.getResposta().contains("82"));
        assertEquals("MathAgent", dto.getFontAgentResposta());
    }

    @Test
    void rejectInvalidExpression() {
        MathAgentService s = new MathAgentService();
        AgentResponseDTO dto = s.process("70 + alert('xss')", "conv-1", "user-1");
        assertTrue(dto.getResposta().contains("Não consegui processar"));
    }
    
}
