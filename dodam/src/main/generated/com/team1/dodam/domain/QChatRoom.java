package com.team1.dodam.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = 1222611373L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final QTimestamped _super = new QTimestamped(this);

    public final EnumPath<com.team1.dodam.shared.ChatRoomStatus> chatRoomStatus = createEnum("chatRoomStatus", com.team1.dodam.shared.ChatRoomStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath itemImageUrl = createString("itemImageUrl");

    public final StringPath itemName = createString("itemName");

    public final ListPath<ChatMessage, QChatMessage> messageList = this.<ChatMessage, QChatMessage>createList("messageList", ChatMessage.class, QChatMessage.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QPost post;

    public final StringPath postOwnerImageUrl = createString("postOwnerImageUrl");

    public final StringPath postOwnerLocation = createString("postOwnerLocation");

    public final StringPath postOwnerNickname = createString("postOwnerNickname");

    public final StringPath requestDealUserImageUrl = createString("requestDealUserImageUrl");

    public final StringPath requestDealUserLocation = createString("requestDealUserLocation");

    public final StringPath requestDealUserNickname = createString("requestDealUserNickname");

    public final StringPath roomId = createString("roomId");

    public final StringPath roomName = createString("roomName");

    public final QUser user1;

    public final QUser user2;

    public QChatRoom(String variable) {
        this(ChatRoom.class, forVariable(variable), INITS);
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoom(PathMetadata metadata, PathInits inits) {
        this(ChatRoom.class, metadata, inits);
    }

    public QChatRoom(Class<? extends ChatRoom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.user1 = inits.isInitialized("user1") ? new QUser(forProperty("user1")) : null;
        this.user2 = inits.isInitialized("user2") ? new QUser(forProperty("user2")) : null;
    }

}

