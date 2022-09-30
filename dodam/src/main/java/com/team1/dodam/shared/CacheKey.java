package com.team1.dodam.shared;

public class CacheKey {
    private CacheKey() {}
    public static final int DEFAULT_EXPIRE_SEC = 60;
    public static final String CHATROOMS = "chatrooms";
    public static final int CHATROOMS_EXPIRE_SEC = 20;
    public static final String CHATROOM = "chatroom";
    public static final int CHATROOM_EXPIRE_SEC = 30;
    public static final String MYPAGEPOSTS = "mypage::posts";
    public static final int MYPAGEPOSTS_EXPIRE_SEC = 30;
    public static final String MYPAGEPICKS = "mypage::picks";
    public static final int MYPAGEPICKS_EXPIRE_SEC = 30;
    public static final String POSTS = "posts";
    public static final int POSTS_EXPIRE_SEC = 1800;
    public static final String POST = "post";
    public static final int POST_EXPIRE_SEC = 60;
}
