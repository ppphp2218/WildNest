package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 酒品分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("drink_category")
public class DrinkCategory extends BaseEntity {
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;
    
    /**
     * 排序值，数值越小越靠前
     */
    private Integer sortOrder;
    
    /**
     * 分类图标URL
     */
    private String iconUrl;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}