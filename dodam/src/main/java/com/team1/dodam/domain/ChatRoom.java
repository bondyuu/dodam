package com.team1.dodam.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ChatRoom extends Timestamped implements Serializable{

    private static final long serialVersionUID = 6494678977089006639L;
    @Id
    private String roomId;
    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "chatRoomList")
//    private List<User> userList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messageList;


//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChatMessage> messages;

    public static ChatRoom create(User postUser, User loginUser, Post post) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.title = postUser.getNickname()+ "과" + loginUser.getNickname() + "의 채팅방";
        chatRoom.post = post;
        chatRoom.user1 = postUser;
        chatRoom.user2 = loginUser;
        return chatRoom;
    }
}
