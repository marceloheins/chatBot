package com.marcelo.chatBot.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConversationRepository {

    private static final int MAX_MESSAGES = 200;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ListOperations<String, Object> listOps;

    public ConversationRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOps = redisTemplate.opsForList();
    }

    public void saveMessage(String conversationId, Object payload) {
        String key = "conversation:" + conversationId;
        listOps.rightPush(key, payload);
        Long size = listOps.size(key);
        if (size != null && size > MAX_MESSAGES){
            long trimStart = size - MAX_MESSAGES;
            redisTemplate.opsForList().trim(key, trimStart, - 1);
        }
    }

    public List<Object> getConversationHistory(String conversationId) {
        String key = "conversation:" + conversationId;
        return listOps.range(key, 0, -1);
    }
    
}
