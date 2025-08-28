package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 推荐规则实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recommendation")
public class Recommendation extends BaseEntity {
    
    /**
     * 规则名称
     */
    private String name;
    
    /**
     * 规则描述
     */
    private String description;
    
    /**
     * 选项组合(JSON格式，存储选项ID数组)
     */
    private String optionCombination;
    
    /**
     * 目标酒品ID
     */
    private Long drinkId;
    
    /**
     * 推荐理由
     */
    private String reason;
    
    /**
     * 匹配权重
     */
    private BigDecimal matchWeight;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}