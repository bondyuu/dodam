package com.team1.dodam.controller;

import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.repository.ChatRoomRepository;
import com.team1.dodam.service.ChatRoomCacheService;
import com.team1.dodam.shared.CacheKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"[Redis Cache] 채팅방 전체 조회/상세 조회/삭제"})
@RequiredArgsConstructor
@RestController
@RequestMapping("chat/user")
public class ChatRoomCacheController {

    private final ChatRoomCacheService chatRoomCacheService;

    @ApiOperation(value = "채팅방 전체 조회 메소드")
    @Cacheable(value = CacheKey.CAHTROOM, key = "#userId", unless = "#result == null")
    @GetMapping("/{userId}/chatrooms")
    public ResponseDto<?> findChatroomAllInRedisOrMySQL(@PathVariable (name = "userId") Long userId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomCacheService.findChatroomAllInRedisOrMySQL(userId, userDetails);
    }

    @ApiOperation(value = "채팅방 상세 조회 메소드")
    @Cacheable(value = CacheKey.CAHTROOM, key = "#roomId", unless = "#result == null")
    @GetMapping("/{userId}/chatroom/{roomId}")
    public ResponseDto<?> findDetailChatroomInRedisOrMySQL(@PathVariable (name = "userId") Long userId,
                                  @PathVariable (name = "roomId") String roomId,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomCacheService.findDetailChatroomInRedisOrMySQL(userId, roomId, userDetails);
    }

    // Redis 채팅방 삭제 : Redis에서 해당 채팅방 캐시 삭제 및 MySQL에서 해당 채팅방의 Status를 Activated에서 Deleted로 변경
    @ApiOperation(value = "채팅방 삭제 메소드")
    @CacheEvict(value = CacheKey.CAHTROOM, key = "#userId")
    @DeleteMapping("/{userId}/chatroom/{roomId}")
    public ResponseDto<?> deleteChatroomInRedisAndMySQL(@PathVariable (name = "userId") Long userId,
                                                        @PathVariable (name = "roomId") String roomId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomCacheService.deleteChatroomInRedisAndMySQL(userId, roomId, userDetails);
    }
}
