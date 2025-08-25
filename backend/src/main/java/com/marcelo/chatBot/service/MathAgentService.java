package com.marcelo.chatBot.service;

import org.springframework.stereotype.Service;
import com.marcelo.chatBot.logging.StructuredLogger;


import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import com.marcelo.chatBot.dto.AgentResponseDTO;



import java.util.HashMap;
import java.util.Map;

@Service
public class MathAgentService {
   

    public AgentResponseDTO process(String message, String conversationId, String userId){
        long start =  System.currentTimeMillis();
        String resposta;
        long execMs = 0;

        try{
            String cleaned = message.replace(',','.')
            .replaceAll("\\\u00D7|x", "*")
            .replaceAll("(?i)\\b(x)\\b", "*")
            .trim();

            Expression expr = new ExpressionBuilder(cleaned).build();
            double result = expr.evaluate();
            

            if (Math.floor(result) == result){
                resposta =  String.format("O resultado é: %.0f", result);

            }else{
                resposta = String.format("O rsultado é: %s", stripTrailingZeros(result));

            }
        }catch (Exception ex){
            resposta = "Não consegui processar a expressão matematica ";
            Map<String, Object> f = new HashMap<>();
            f.put("agent", "MATH");
            f.put("error", ex.getMessage()) ;
            StructuredLogger.error("math.error",f,ex);
        }

        execMs = System.currentTimeMillis() -  start;

        Map<String, Object> fields = new HashMap<>();
        fields.put("agent", "MATH");
        fields.put("execution_time_ms",execMs);
        fields.put("expression", message);
        StructuredLogger.info("math.process",fields);
        

        return AgentResponseDTO.builder()
            .resposta(resposta)
            .fontAgentResposta("MathAgent")
            .executionTimeMs(execMs)
            .build();

    }

    private String stripTrailingZeros(double val){
        String s = Double.toString(val);
        if (s.indexOf('E') >= 0) return s;
        if (s.contains(".")){
            s = s.replaceAll("\\.?0+$","");

        }
        return s;
    }
}
