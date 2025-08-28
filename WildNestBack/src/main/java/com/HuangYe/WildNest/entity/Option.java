package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 推荐选项实体类
 * 
 * @author HuangYe
 * @since 2024
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`option`")
public class Option extends BaseEntity {

    /**
     * 问题ID，外键关联question.id
     */
    @TableField("question_id")
    private Long questionId;

    /**
     * 选项内容
     */
    @TableField("content")
    private String content;

    /**
     * 权重值，范围0.10-2.00
     */
    @TableField("weight_value")
    private BigDecimal weightValue;

    /**
     * 关联标签，逗号分隔（如：清爽,果味,低度）
     */
    @TableField("tag_keywords")
    private String tagKeywords;

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
     * 获取标签数组
     * 
     * @return 标签数组
     */
    public String[] getTagArray() {
        if (tagKeywords == null || tagKeywords.trim().isEmpty()) {
            return new String[0];
        }
        return tagKeywords.split(",");
    }

    /**
     * 设置标签数组
     * 
     * @param tags 标签数组
     */
    public void setTagArray(String[] tags) {
        if (tags == null || tags.length == 0) {
            this.tagKeywords = null;
        } else {
            this.tagKeywords = String.join(",", tags);
        }
    }

    /**
     * 检查是否包含指定标签
     * 
     * @param tag 标签
     * @return 是否包含
     */
    public boolean hasTag(String tag) {
        if (tagKeywords == null || tag == null) {
            return false;
        }
        String[] tags = getTagArray();
        for (String t : tags) {
            if (t.trim().equals(tag.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取权重值的double类型
     * 
     * @return 权重值
     */
    public double getWeightAsDouble() {
        return weightValue != null ? weightValue.doubleValue() : 1.0;
    }
}