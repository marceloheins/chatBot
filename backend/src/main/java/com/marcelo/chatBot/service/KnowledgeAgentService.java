package com.marcelo.chatBot.service;

import com.marcelo.chatBot.dto.AgentResponseDTO;
import com.marcelo.chatBot.dto.ChatMessage;
import com.marcelo.chatBot.logging.StructuredLogger;
import com.marcelo.chatBot.redis.ConversationRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class KnowledgeAgentService {
    //novo
    private final ConversationRepository conversationRepository;
    public KnowledgeAgentService(ConversationRepository conversationRepository){
        this.conversationRepository = conversationRepository;

    }
    public AgentResponseDTO process (String message, String conversationId, String UserId ){
        long start = System.currentTimeMillis();

        String[] respostaExemplo= {
              "A taxa da maquininha é de 2,5% por transação.",
              "Sim, você pode usar seu celular como maquininha com nosso app.",
              "Consulte nosso site para mais detalhes: https://ajuda.infinitepay.io/pt-BR/"
        };
        String resposta = respostaExemplo[ThreadLocalRandom.current().nextInt(respostaExemplo.length)];
        long execMs =  System.currentTimeMillis() - start;

        Map<String, Object> fields =  new HashMap<>();
        fields.put("agent", "KNOWLEDGE");
        fields.put("execution_time_ms", execMs);
        fields.put("sources",new String[] {"https://ajuda.infinitepay.io/pt-BR/"});
        fields.put("query", message);
        StructuredLogger.info("knowledge.process", fields);
    
        //novo
        conversationRepository.saveMessage(conversationId, new ChatMessage("KnowledgeAgent", resposta, Instant.now().toString()));
        
        
        return AgentResponseDTO.builder()
        .resposta(resposta)
        .fontAgentResposta("KnowledgeAgent")
        .executionTimeMs(execMs)
        .build();
    
    }

}
