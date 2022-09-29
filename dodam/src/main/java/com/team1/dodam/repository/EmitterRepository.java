package com.team1.dodam.repository;

import com.team1.dodam.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EmitterRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, SseEmitter> opsHashEmitter;
    private ValueOperations<String, SseEmitter> opsValueEmitter;
    private static final String EMITTER = "EMITTER";

    @PostConstruct
    private void init() {
        opsHashEmitter = redisTemplate.opsForHash();
    }

    public SseEmitter save(Long userId, SseEmitter emitter) {
        opsHashEmitter.put(EMITTER, String.valueOf(userId), emitter);
        return emitter;
    }
    public SseEmitter findEmitter(String userId){
        return opsHashEmitter.get(EMITTER, userId);
    }
    public void deleteById(Long userId) {
        opsHashEmitter.delete(EMITTER, String.valueOf(userId));
    }
    public Map<String, SseEmitter> findAllStartWithId(String userId) {
        return opsHashEmitter.entries(userId);
    }
}
