package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 酒品信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("drink")
public class Drink extends BaseEntity {
    
    /**
     * 酒品名称
     */
    private String name;
    
    /**
     * 英文名称
     */
    private String englishName;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 酒精度数(%)
     */
    private BigDecimal alcoholContent;
    
    /**
     * 酒品描述
     */
    private String description;
    
    /**
     * 原料成分
     */
    private String ingredients;
    
    /**
     * 口感描述
     */
    private String tasteNotes;
    
    /**
     * 主图片URL
     */
    private String imageUrl;
    
    /**
     * 图片集合(JSON数组)
     */
    private String galleryUrls;
    
    /**
     * 标签，逗号分隔
     */
    private String tags;
    
    /**
     * 是否推荐：1-是，0-否
     */
    private Boolean isFeatured;
    
    /**
     * 是否可售：1-是，0-否
     */
    private Boolean isAvailable;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 排序值
     */
    private Integer sortOrder;
}