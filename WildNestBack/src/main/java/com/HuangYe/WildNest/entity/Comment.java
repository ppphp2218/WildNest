package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 留言实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class Comment extends BaseEntity {
    
    /**
     * 用户昵称
     */
    private String username;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 留言内容
     */
    private String content;
    
    /**
     * 留言分类：default-默认，hot-热门，latest-最新
     */
    private String category;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 回复数
     */
    private Integer replyCount;
    
    /**
     * 是否置顶：1-是，0-否
     */
    private Boolean isPinned;
    
    /**
     * 是否敏感：1-是，0-否
     */
    private Boolean isSensitive;
    
    /**
     * 用户IP地址
     */
    private String userIp;
    
    /**
     * 用户代理信息
     */
    private String userAgent;
    
    /**
     * 设备ID（用于防重复点赞）
     */
    private String deviceId;
    
    /**
     * 状态：1-正常，0-删除，2-审核中
     */
    private Integer status;
}