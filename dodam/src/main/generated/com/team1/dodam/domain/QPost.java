package com.team1.dodam.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 1104988922L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final QTimestamped _super = new QTimestamped(this);

    public final EnumPath<com.team1.dodam.shared.Category> category = createEnum("category", com.team1.dodam.shared.Category.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Image, QImage> imageList = this.<Image, QImage>createList("imageList", Image.class, QImage.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> postPickCount = createNumber("postPickCount", Integer.class);

    public final ListPath<PostPick, QPostPick> postPickList = this.<PostPick, QPostPick>createList("postPickList", PostPick.class, QPostPick.class, PathInits.DIRECT2);

    public final EnumPath<com.team1.dodam.shared.PostStatus> postStatus = createEnum("postStatus", com.team1.dodam.shared.PostStatus.class);

    public final NumberPath<Integer> postVisitCount = createNumber("postVisitCount", Integer.class);

    public final StringPath title = createString("title");

    public final QUser user;

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

