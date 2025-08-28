package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 问题实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class Question extends BaseEntity {
    
    /**
     * 问题标题
     */
    private String title;
    
    /**
     * 问题描述
     */
    private String description;
    
    /**
     * 问题类型：single-单选，multiple-多选
     */
    private String questionType;
    
    /**
     * 排序值，决定问题出现顺序
     */
    private Integer sortOrder;
    
    /**
     * 是否必答：1-是，0-否
     */
    private Integer isRequired;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}