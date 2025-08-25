package com.marcelo.chatBot.controller;

import com.marcelo.chatBot.agent.RouterAgentService;
import com.marcelo.chatBot.dto.*;
import com.marcelo.chatBot.logging.StructuredLogger;
import com.marcelo.chatBot.redis.ConversationRepository;
import com.marcelo.chatBot.service.KnowledgeAgentService;
import com.marcelo.chatBot.service.MathAgentService;
import com.marcelo.chatBot.service.SanitizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = {"*"})
public class ChatController {

    private final RouterAgentService routerAgent;
    private final MathAgentService mathAgent;
    private final KnowledgeAgentService knowledgeAgent;
    private final ConversationRepository conversationRepository;
    private final SanitizationService sanitizationService;
    
    @Autowired
    public ChatController(RouterAgentService routerAgent,
                          MathAgentService mathAgent,
                          KnowledgeAgentService knowledgeAgent,
                          ConversationRepository conversationRepository,
                          SanitizationService sanitizationService){
        this.routerAgent = routerAgent;
        this.mathAgent = mathAgent;
        this.knowledgeAgent = knowledgeAgent;
        this.conversationRepository = conversationRepository;
        this.sanitizationService = sanitizationService;
    }


    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        System.out.println(">>> Chegou ate o request: " + request);
        String requestId = "req-" + System.currentTimeMillis();
        StructuredLogger.setContext(requestId, request.getConversationId(), request.getIdUsuario());


        // Sanitização e defesa mínima de prompt-injection
        String sanitized = sanitizationService.sanitizeUserText(request.getMensagem());
        if (sanitizationService.isLikelyPromptInjection(sanitized)) {
            StructuredLogger.clearContext();
            return ResponseEntity.badRequest().body(ChatResponse.builder()
                    .resposta("Sua mensagem parece conter instruções suspeitas. Tente reformular.")
                    .fontAgentResposta("Guard")
                    .agenteFluxoTrabalho(Collections.singletonList(new AgentDecision("Guard", "blocked_prompt_injection")))
                    .build());
        }

        List<AgentDecision> workflow = new ArrayList<>();

        // persistir entrada no histórico (poderia salvar uma estrutura com timestamp)
        Map<String,Object> entry = new HashMap<>();
        entry.put("who","user");
        entry.put("message", request.getMensagem());
        entry.put("userId", request.getIdUsuario());
        entry.put("timestamp", System.currentTimeMillis());
        conversationRepository.saveMessage(request.getConversationId(), entry);
        
        // decisão do roteador
        DecisionResult decisionResult = routerAgent.route(request.getMensagem());
        workflow.add(new AgentDecision("RouterAgent", decisionResult.getAgent().name()));
        
        // executar agente
        AgentResponseDTO agentResponse;
        switch (decisionResult.getAgent()) {
            case MATH:
                agentResponse = mathAgent.process(request.getMensagem(), request.getConversationId(), request.getIdUsuario());
                workflow.add(new AgentDecision("MathAgent", null));
                break;
            case KNOWLEDGE:
                agentResponse = knowledgeAgent.process(request.getMensagem(), request.getConversationId(), request.getIdUsuario());
                workflow.add(new AgentDecision("KnowledgeAgent", null));
                break;
            default:
                agentResponse = AgentResponseDTO.builder()
                        .resposta("Não foi possível identificar o agente.")
                        .fontAgentResposta("RouterAgent")
                        .executionTimeMs(0)
                        .build();
                workflow.add(new AgentDecision("None", null));
        }
        // persistir resposta
        Map<String,Object> respEntry = new HashMap<>();
        respEntry.put("who","bot");
        respEntry.put("message", agentResponse.getResposta());
        respEntry.put("agent", agentResponse.getFontAgentResposta());
        respEntry.put("executionTimeMs", agentResponse.getExecutionTimeMs());
        respEntry.put("timestamp", System.currentTimeMillis());
        conversationRepository.saveMessage(request.getConversationId(), respEntry);


       

        // log final
        Map<String,Object> logFields = new HashMap<>();
        logFields.put("agent", "RouterAgent");
        logFields.put("decision", decisionResult.getAgent().name());
        logFields.put("execution_time_ms", 0);
        StructuredLogger.info("router.decision", logFields);
        

        // limpar contexto
        StructuredLogger.clearContext();

        ChatResponse response = ChatResponse.builder()
                .resposta(agentResponse.getResposta())
                .fontAgentResposta(agentResponse.getFontAgentResposta())
                .agenteFluxoTrabalho(agentResponse.getAgentWorkflow())
                .build();

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<Object>> history(@PathVariable String conversationId) {
        List<Object> history =  conversationRepository.getConversationHistory(conversationId);
        return ResponseEntity.ok(history);
    }
    
}