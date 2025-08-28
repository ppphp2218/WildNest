package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回复实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reply")
public class Reply extends BaseEntity {
    
    /**
     * 留言ID
     */
    private Long commentId;
    
    /**
     * 父回复ID，0表示直接回复留言
     */
    private Long parentId;
    
    /**
     * 回复者昵称
     */
    private String username;
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 用户IP地址
     */
    private String userIp;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 状态：1-正常，0-删除
     */
    private Integer status;
}