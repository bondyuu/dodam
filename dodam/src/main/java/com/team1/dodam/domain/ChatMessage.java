package com.team1.dodam.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ChatMessage extends Timestamped {

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom; // 방번호

    @Column
    private MessageType type; // 메시지 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 메시지 보낸사람

    @Column
    private String message; // 메시지

}