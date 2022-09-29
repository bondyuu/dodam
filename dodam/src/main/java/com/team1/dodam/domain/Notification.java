package com.team1.dodam.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @Column
    private String content;

    @Column
    private Boolean isRead;

    public void changeIsRead() {
        this.isRead = true;
    }
    public Notification(User user, ChatRoom chatRoom, String content, Boolean isRead){
        this.user = user;
        this.chatRoom = chatRoom;
        this.content = content;
        this.isRead = isRead;
    }
}
