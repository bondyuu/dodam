package com.team1.dodam.repository;

import com.team1.dodam.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class EmitterRepository {
    private final RedisTemplate<String, SseEmitter> redisTemplate;
    private HashOperations<String, String, SseEmitter> opsHashEmitter;
    private static final String EMITTER = "EMITTER";

    @PostConstruct
    private void init() {
        opsHashEmitter = redisTemplate.opsForHash();
    }

    public SseEmitter save(Long userId, String id, SseEmitter emitter) {
        opsHashEmitter.put(String.valueOf(userId), id, emitter);
        return emitter;
    }

    public void deleteById(Long userId, String id) {
        opsHashEmitter.delete(String.valueOf(userId), id);
    }
    public Map<String, SseEmitter> findAllStartWithId(String userId) {
        return opsHashEmitter.entries(userId);
    }
}
