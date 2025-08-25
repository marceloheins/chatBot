package com.marcelo.chatBot.logging;

import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.MDC;

public class StructuredLogger {
    private static final Logger logger = LoggerFactory.getLogger("structured");
    private static final ObjectMapper mapper =  new ObjectMapper();

    private StructuredLogger(){}

    public static void setContext(String requestId, String conversationId, String userId ){
        if (requestId !=null) MDC.put("requestId", requestId);
        if (conversationId != null) MDC.put("conversation_id", conversationId);
        if (userId != null) MDC.put("user_id", userId);

    }

    public static void clearContext(){
        MDC.remove("requestId");
        MDC.remove("conversation_id");
        MDC.remove("user_id");
    }

    public static void info(String event, Map<String, Object> fields){
        fields.putIfAbsent("timestamp", Instant.now().toString());
        fields.putIfAbsent("level", "INFO");
        fields.putIfAbsent("event", event);
        try{
            String json =  mapper.writeValueAsString(fields);
            logger.info(json);

        }catch (JsonProcessingException e){
            logger.info("{} - {}", event, fields.toString());

        }
    }

    public static void error(String event, Map<String, Object> fields, Throwable t){
        fields.putIfAbsent("timestamp", Instant.now().toString());
        fields.putIfAbsent("level", "ERROR");
        fields.putIfAbsent("event", event);
        try{
            String json =  mapper.writeValueAsString(fields);
            logger.error(json, t);

        }catch (JsonProcessingException e){
            logger.info("{} - {}", event, fields.toString());

        }

    }

    
}
