package com.team1.dodam.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team1.dodam.shared.ChatRoomStatus;
import com.team1.dodam.shared.PostStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ChatRoom extends Timestamped implements Serializable{

    private static final long serialVersionUID = 6494678977089006639L;
    @Id
    private String roomId;
    @Column
    private String roomName;
//    @Column
//    private String dealerImageUrl;
//    @Column
//    private String dealerNickname;
//    @Column
//    private String dealerLocation;
    @Column
    private String postOwnerImageUrl;
    @Column
    private String postOwnerNickname;
    @Column
    private String postOwnerLocation;
    @Column
    private String requestDealUserImageUrl;
    @Column
    private String requestDealUserNickname;
    @Column
    private String requestDealUserLocation;
    @Column
    private String itemName;
    @Column
    private String itemImageUrl;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ChatRoomStatus chatRoomStatus;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonManagedReference
    @JsonBackReference(value = "post-chatroom")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "chatRoomList")
//    private List<User> userList = new ArrayList<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonManagedReference
    @JsonBackReference(value = "user1-chatroom")
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonManagedReference
    @JsonBackReference(value = "user2-chatroom")
    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonBackReference
    @JsonManagedReference(value = "chatroom-chatmessage")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private List<ChatMessage> messageList;


//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChatMessage> messages;

    public static ChatRoom create(User postUser, User loginUser, Post post) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
//        chatRoom.title = postUser.getNickname()+ "과" + loginUser.getNickname() + "의 채팅방";

        chatRoom.roomName = postUser.getNickname() + ":" + loginUser.getNickname() + ":" + chatRoom.roomId;
//        chatRoom.roomName = "chatroom::" + chatRoom.roomId;
//        chatRoom.roomName = chatRoom.roomId;

//        chatRoom.dealerImageUrl = postUser.getProfileUrl();
//        chatRoom.dealerNickname = Objects.equals(loginUser.getNickname(), post.getUser().getNickname()) ? loginUser.getNickname() : postUser.getNickname();
//        chatRoom.dealerLocation = postUser.getLocation();

        chatRoom.postOwnerImageUrl = postUser.getProfileUrl();
        chatRoom.postOwnerNickname = postUser.getNickname();
        chatRoom.postOwnerLocation = postUser.getLocation();
        chatRoom.requestDealUserImageUrl = loginUser.getProfileUrl();
        chatRoom.requestDealUserNickname = loginUser.getNickname();
        chatRoom.requestDealUserLocation = loginUser.getLocation();

        chatRoom.itemName = post.getTitle();
        chatRoom.itemImageUrl = post.getImageList().get(0).getImageUrl();
        chatRoom.post = post;
        chatRoom.user1 = postUser;
        chatRoom.user2 = loginUser;
        chatRoom.chatRoomStatus = ChatRoomStatus.ACTIVATED;
        return chatRoom;
    }

    public void updatePostStatusToDeleted() { this.chatRoomStatus = ChatRoomStatus.DELETED; }
}
