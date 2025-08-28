package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("like_record")
public class LikeRecord extends BaseEntity {
    
    /**
     * 留言ID
     */
    private Long commentId;
    
    /**
     * 用户IP地址
     */
    private String userIp;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 用户指纹（浏览器特征）
     */
    private String userFingerprint;
}