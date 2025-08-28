package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 选项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("option")
public class Option extends BaseEntity {
    
    /**
     * 问题ID
     */
    private Long questionId;
    
    /**
     * 选项内容
     */
    private String content;
    
    /**
     * 选项描述
     */
    private String description;
    
    /**
     * 权重值，用于推荐算法
     */
    private BigDecimal weight;
    
    /**
     * 排序值
     */
    private Integer sortOrder;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}