//package com.team1.dodam.util;
//
//import com.team1.dodam.domain.ChatRoom;
//import com.team1.dodam.domain.Post;
//import com.team1.dodam.repository.ChatRoomRepository;
//import com.team1.dodam.repository.PostRepository;
//import com.team1.dodam.service.ChatRoomCacheService;
//import com.team1.dodam.service.ChatRoomService;
//import com.team1.dodam.service.PostService;
//import com.team1.dodam.shared.ChatRoomStatus;
//import com.team1.dodam.shared.PostStatus;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.Period;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class Scheduler {
//    private final PostRepository postRepository;
//    private final PostService postService;
//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatRoomCacheService chatRoomCacheService;
//
//    // 매월 매주 매일 새벽 1시에 Post 관련 Schedule 작업 수행
//    @Transactional
//    @Scheduled(cron = "0 0 1 * * *")
//    public void deletePosts() {
//        List<Post> postList = postRepository.findAllByPostStatus(PostStatus.DELETED);
//
//        for(Post post : postList) {
//            LocalDate from = LocalDate.of(post.getModifiedAt().getYear(), post.getModifiedAt().getMonthValue(), post.getModifiedAt().getDayOfMonth());
//            LocalDate to = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
//
//            Period period = Period.between(from, to);
//
//            if (period.getDays() >= 7) {
//                postService.deletePosts(post.getId(), post.getUser().getNickname());
//            }
//        }
//    }
//
//    // 매월 매주 매일 새벽 2시에 ChatRoom 관련 Schedule 작업 수행
//    @Transactional
//    @Scheduled(cron = "0 0 2 * * *")
//    public void deleteChatRooms() {
//        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByChatRoomStatus(ChatRoomStatus.DELETED);
//
//        for(ChatRoom chatRoom : chatRoomList) {
//            LocalDate from = LocalDate.of(chatRoom.getModifiedAt().getYear(), chatRoom.getModifiedAt().getMonthValue(), chatRoom.getModifiedAt().getDayOfMonth());
//            LocalDate to = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
//
//            Period period = Period.between(from, to);
//
//            if (period.getDays() >= 7) {
//                chatRoomCacheService.deleteChatRoomsInMySQL(chatRoom.getRoomId());
//            }
//        }
//    }
//}
