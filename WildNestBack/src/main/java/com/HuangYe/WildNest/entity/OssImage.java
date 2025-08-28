package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OSS图片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oss_image")
public class OssImage extends BaseEntity {
    
    /**
     * 关联留言ID
     */
    private Long commentId;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 存储文件名
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 访问URL
     */
    private String fileUrl;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件类型(jpg,png,gif等)
     */
    private String fileType;
    
    /**
     * 图片宽度
     */
    private Integer width;
    
    /**
     * 图片高度
     */
    private Integer height;
    
    /**
     * 上传IP
     */
    private String uploadIp;
    
    /**
     * 状态：1-正常，0-删除
     */
    private Integer status;
}