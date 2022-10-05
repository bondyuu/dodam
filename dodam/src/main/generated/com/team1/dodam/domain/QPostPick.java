package com.team1.dodam.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostPick is a Querydsl query type for PostPick
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostPick extends EntityPathBase<PostPick> {

    private static final long serialVersionUID = 542159419L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostPick postPick = new QPostPick("postPick");

    public final QTimestamped _super = new QTimestamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QPost post;

    public final QUser user;

    public QPostPick(String variable) {
        this(PostPick.class, forVariable(variable), INITS);
    }

    public QPostPick(Path<? extends PostPick> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostPick(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostPick(PathMetadata metadata, PathInits inits) {
        this(PostPick.class, metadata, inits);
    }

    public QPostPick(Class<? extends PostPick> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

