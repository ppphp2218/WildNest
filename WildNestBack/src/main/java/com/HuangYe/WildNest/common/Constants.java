package com.HuangYe.WildNest.common;

/**
 * 系统常量类
 */
public class Constants {
    
    /**
     * 响应状态码
     */
    public static class ResponseCode {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
    

    
    /**
     * 缓存相关常量
     */
    public static class Cache {
        public static final String DRINK_CACHE_PREFIX = "drink:";
        public static final String COMMENT_CACHE_PREFIX = "comment:";
        public static final long DEFAULT_EXPIRE_TIME = 30 * 60; // 30分钟
    }
    
    /**
     * 分页相关常量
     */
    public static class Page {
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int MAX_PAGE_SIZE = 100;
    }
    
    /**
     * 文件上传相关常量
     */
    public static class Upload {
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
        public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
    }
}