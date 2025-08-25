package com.marcelo.chatBot;

import com.marcelo.chatBot.agent.RouterAgentService;
import com.marcelo.chatBot.dto.DecisionResult;
import com.marcelo.chatBot.model.Agent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RouterAgentServiceTest {

    @Test
     void routeMathByExpression() {
        RouterAgentService r = new RouterAgentService();
        DecisionResult dr = r.route("70 + 12");
        assertEquals(Agent.MATH, dr.getAgent());
    }

    @Test
    void routeKnowledgeByDefault() {
        RouterAgentService r = new RouterAgentService();
        DecisionResult dr = r.route("Qual a taxa da maquininha?");
        assertEquals(Agent.KNOWLEDGE, dr.getAgent());
    }
}
