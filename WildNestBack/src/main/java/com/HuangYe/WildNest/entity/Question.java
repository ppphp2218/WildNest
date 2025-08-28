package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 推荐问题实体类
 * 
 * @author HuangYe
 * @since 2024
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class Question extends BaseEntity {
    
    /**
     * 问题标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 问题描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 问题类型：single-单选，multiple-多选
     */
    @TableField("question_type")
    private String questionType;
    
    /**
     * 排序值，数值越小越靠前
     */
    @TableField("sort_order")
    private Integer sortOrder;
    
    /**
     * 是否启用：1-启用，0-禁用
     */
    @TableField("is_active")
    private Boolean isActive;
    
    /**
     * 问题选项列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Option> options;
    
    /**
     * 问题类型枚举
     */
    public enum QuestionType {
        SINGLE("single", "单选"),
        MULTIPLE("multiple", "多选");
        
        private final String code;
        private final String description;
        
        QuestionType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static QuestionType fromCode(String code) {
            for (QuestionType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return SINGLE; // 默认单选
        }
    }
}