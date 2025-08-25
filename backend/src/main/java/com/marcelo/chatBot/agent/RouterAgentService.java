package com.marcelo.chatBot.agent;



import com.marcelo.chatBot.dto.DecisionResult;
import com.marcelo.chatBot.model.Agent;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class RouterAgentService{

    private static final Pattern SIMPLE_MATH_PATTERN =
        Pattern.compile(".*(\\d+\\s*[xX×\\*\\+\\-\\/]\\s*\\d+).*");

    private static final Pattern MATH_PHRASE_PATTERN =
            Pattern.compile(".*\\b(how much is|quanto é|quanto custa|how many|quanto)\\b.*",
             Pattern.CASE_INSENSITIVE);

    private static final Pattern ALMOST_EXPRESSION =
            Pattern.compile("^[0-9\\s\\(\\)\\+\\-\\*\\/\\.,xX×^%]+$");

    /*    
    private final MathAgentService mathAgent;
    private final ConversationRepository conversationRepository;
    private final KnowledgeAgentService knowledgeAgent;

    public RouterAgentService (MathAgentService mathAgent,
                                KnowledgeAgentService knowledgeAgent,
                                ConversationRepository conversationRepository){
                                    this.mathAgent = mathAgent;
                                    this.knowledgeAgent = knowledgeAgent;
                                    this.conversationRepository = conversationRepository;

     }

    public  AgentResponseDTO route (String conversationId, String userId, String message){
        conversationRepository.saveMessage(conversationId, new ChatMessage("User", message, Instant.now().toString()));


        DecisionResult decision = decide(message);

        if(decision.getAgent() == Agent.MATH){
            return mathAgent.process(message,conversationId, userId);
        }else if (decision.getAgent() == Agent.KNOWLEDGE){
            return knowledgeAgent.process(message,conversationId, userId);
        }
        return AgentResponseDTO.builder()
                .resposta("Não identifiquei o tipo de mensagem.")
                .fontAgentResposta("RouterAgent")
                .executionTimeMs(0)
                .build();
    }*/

    public DecisionResult route(String mensagemBruta){
        return decide(mensagemBruta);
    }

    private DecisionResult decide (String mensagemBruta){
        if (mensagemBruta == null || mensagemBruta.isBlank()){
            return new DecisionResult(Agent.UNKNOWN, "empty_or_null_message");

        }
        String normalized = mensagemBruta.trim().toLowerCase(Locale.ROOT);

        if (MATH_PHRASE_PATTERN.matcher(normalized).matches()){
            return new DecisionResult(Agent.MATH, "matched_math_phrase");
        }
        if(SIMPLE_MATH_PATTERN.matcher(normalized).matches()){
            return new DecisionResult(Agent.MATH, "matched_simple_mayh_pattern");
        }
        if (ALMOST_EXPRESSION.matcher(normalized).matches()){
            return new DecisionResult(Agent.MATH, "matches_almost_expression");
        }
        return new DecisionResult(Agent.KNOWLEDGE,"default_to_knowledge");
    }
}







/* 
@Service
public class RouterAgentService {

    private final MathAgentService mathAgent;
    private final KnowledgeAgentService knowledgeAgent;
    private final ConversationRepository conversationRepository;

    public RouterAgentService(MathAgentService mathAgent,
                              KnowledgeAgentService knowledgeAgent,
                              ConversationRepository conversationRepository){
                        this.mathAgent = mathAgent;
                        this.knowledgeAgent = knowledgeAgent;
                        this.conversationRepository = conversationRepository; 
    }

    public AgentResponseDTO route(String conversationId, String userId, String message){
        conversationRepository.saveMessage(conversationId,new ChatMessage("User", message, Instant.now().toString()));
        DecisionResult decision = decide(message);

        if(decision.getAgent()== Agent.MATH){
            return mathAgent.process(message, conversationId, userId);

        }else if (decision.getAgent() == Agent.KNOWLEDGE){
            return knowledgeAgent.process(message, conversationId, userId);

        }
        return AgentResponseDTO.builder()
            .resposta("Não consegui identificar o tipo de mensagem")
            .fontAgentResposta("RouterAgent")
            .executionTimeMs(0)
            .build();
    }
    
    private static final Pattern SIMPLE_MATH_PATTERN =
        Pattern.compile(".*(\\d+\\s*[xX×\\*\\+\\-\\/]\\s*\\d+).*");

    private static final Pattern MATH_PHRASE_PATTERN =
            Pattern.compile(".*\\b(how much is|quanto é|quanto custa|how many|quanto)\\b.*",
             Pattern.CASE_INSENSITIVE);

    private static final Pattern ALMOST_EXPRESSION =
            Pattern.compile("^[0-9\\s\\(\\)\\+\\-\\*\\/\\.,xX×^%]+$");

    public DecisionResult decide(String mensagemBruta) {
        if (mensagemBruta == null || mensagemBruta.isBlank()) {
            return new DecisionResult(Agent.UNKNOWN, "empty_or_null_message");
        }
    String normalized = mensagemBruta.trim().toLowerCase(Locale.ROOT);

    if (MATH_PHRASE_PATTERN.matcher(normalized).matches()){
        return new DecisionResult(Agent.MATH, "matchd_math_phrase");

    }
    if (SIMPLE_MATH_PATTERN.matcher(normalized).matches()){
        return new DecisionResult(Agent.MATH, "matched_simple_math_pattern");

    }
    if (ALMOST_EXPRESSION.matcher(normalized).matches()){
        return new DecisionResult(Agent.MATH, "matches_almost_expression");

    }
    return new DecisionResult(Agent.KNOWLEDGE, "default_to_knowledge");

    }
}
    */
