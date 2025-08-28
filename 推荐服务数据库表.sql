-- WildNest 推荐服务数据库表创建脚本
-- 基于推荐服务实现方案设计
-- 创建时间: 2025-01-28

-- 使用wildnest_db数据库
USE wildnest_db;

-- ====================================
-- 1. 问题表（question）
-- ====================================
CREATE TABLE question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '问题ID，主键',
    title VARCHAR(200) NOT NULL COMMENT '问题标题',
    description TEXT COMMENT '问题描述',
    question_type ENUM('single', 'multiple') DEFAULT 'single' COMMENT '问题类型：single-单选，multiple-多选',
    sort_order INT DEFAULT 0 COMMENT '排序值，数值越小越靠前',
    is_active BIT DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐问题表';

-- 问题表索引
CREATE INDEX idx_question_sort_active ON question(sort_order, is_active);
CREATE INDEX idx_question_type ON question(question_type);
CREATE INDEX idx_question_created_at ON question(created_at);

-- ====================================
-- 2. 选项表（option）
-- ====================================
CREATE TABLE `option` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选项ID，主键',
    question_id BIGINT NOT NULL COMMENT '问题ID，外键关联question.id',
    content VARCHAR(200) NOT NULL COMMENT '选项内容',
    weight_value DECIMAL(3,2) DEFAULT 1.00 COMMENT '权重值，范围0.10-2.00',
    tag_keywords VARCHAR(500) COMMENT '关联标签，逗号分隔（如：清爽,果味,低度）',
    sort_order INT DEFAULT 0 COMMENT '排序值，数值越小越靠前',
    is_active BIT DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐选项表';

-- 选项表索引
CREATE INDEX idx_option_question_sort ON `option`(question_id, sort_order);
CREATE INDEX idx_option_weight ON `option`(weight_value);
CREATE INDEX idx_option_active ON `option`(is_active);
CREATE INDEX idx_option_tags ON `option`(tag_keywords(100));

-- ====================================
-- 3. 推荐规则表（recommendation_rule）
-- ====================================
CREATE TABLE recommendation_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID，主键',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_description TEXT COMMENT '规则描述',
    option_combination JSON NOT NULL COMMENT '选项组合，JSON格式存储选项ID数组',
    target_drink_ids TEXT NOT NULL COMMENT '目标酒品ID列表，逗号分隔',
    match_score DECIMAL(5,2) DEFAULT 0.00 COMMENT '匹配分数，用于规则优先级',
    recommendation_reason VARCHAR(500) COMMENT '推荐理由，显示给用户',
    condition_type ENUM('exact', 'partial', 'fuzzy') DEFAULT 'exact' COMMENT '匹配类型：exact-精确匹配，partial-部分匹配，fuzzy-模糊匹配',
    min_match_count INT DEFAULT 1 COMMENT '最小匹配选项数量',
    is_active BIT DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    priority_level INT DEFAULT 0 COMMENT '优先级，数值越大优先级越高',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐规则表';

-- 推荐规则表索引
CREATE INDEX idx_rule_active_priority ON recommendation_rule(is_active, priority_level DESC);
CREATE INDEX idx_rule_match_score ON recommendation_rule(match_score DESC);
CREATE INDEX idx_rule_condition_type ON recommendation_rule(condition_type);
CREATE INDEX idx_rule_name ON recommendation_rule(rule_name);
CREATE INDEX idx_rule_created_at ON recommendation_rule(created_at);

-- ====================================
-- 4. 推荐日志表（recommendation_log）
-- ====================================
CREATE TABLE recommendation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID，主键',
    session_id VARCHAR(64) COMMENT '会话ID，用于追踪用户推荐会话',
    user_answers JSON NOT NULL COMMENT '用户答案，JSON格式存储问题ID和选项ID的映射',
    recommended_drinks JSON COMMENT '推荐结果，JSON格式存储推荐的酒品信息',
    matched_rules JSON COMMENT '匹配的规则，JSON格式存储匹配到的规则信息',
    algorithm_version VARCHAR(20) DEFAULT 'v1.0' COMMENT '算法版本号',
    total_score DECIMAL(8,2) COMMENT '总推荐分数',
    execution_time_ms INT COMMENT '算法执行时间（毫秒）',
    user_ip VARCHAR(45) COMMENT '用户IP地址',
    user_agent TEXT COMMENT '用户代理信息',
    device_info VARCHAR(200) COMMENT '设备信息',
    user_feedback TINYINT COMMENT '用户反馈：1-满意，0-不满意，NULL-未反馈',
    feedback_reason VARCHAR(500) COMMENT '反馈原因',
    is_test_data BIT DEFAULT 0 COMMENT '是否测试数据：1-是，0-否',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐日志表';

-- 推荐日志表索引
CREATE INDEX idx_log_session_id ON recommendation_log(session_id);
CREATE INDEX idx_log_created_at ON recommendation_log(created_at);
CREATE INDEX idx_log_algorithm_version ON recommendation_log(algorithm_version);
CREATE INDEX idx_log_user_feedback ON recommendation_log(user_feedback);
CREATE INDEX idx_log_user_ip ON recommendation_log(user_ip);
CREATE INDEX idx_log_test_data ON recommendation_log(is_test_data);

-- ====================================
-- 5. 推荐反馈表（recommendation_feedback）
-- ====================================
CREATE TABLE recommendation_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID，主键',
    log_id BIGINT NOT NULL COMMENT '推荐日志ID，外键关联recommendation_log.id',
    drink_id BIGINT COMMENT '用户最终选择的酒品ID，外键关联drink.id',
    feedback_type ENUM('like', 'dislike', 'neutral') NOT NULL COMMENT '反馈类型：like-喜欢，dislike-不喜欢，neutral-一般',
    feedback_score TINYINT COMMENT '反馈评分，1-5分',
    feedback_content TEXT COMMENT '反馈内容',
    improvement_suggestions TEXT COMMENT '改进建议',
    user_ip VARCHAR(45) COMMENT '用户IP地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (log_id) REFERENCES recommendation_log(id) ON DELETE CASCADE,
    FOREIGN KEY (drink_id) REFERENCES drink(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐反馈表';

-- 推荐反馈表索引
CREATE INDEX idx_feedback_log_id ON recommendation_feedback(log_id);
CREATE INDEX idx_feedback_drink_id ON recommendation_feedback(drink_id);
CREATE INDEX idx_feedback_type ON recommendation_feedback(feedback_type);
CREATE INDEX idx_feedback_score ON recommendation_feedback(feedback_score);
CREATE INDEX idx_feedback_created_at ON recommendation_feedback(created_at);

-- ====================================
-- 6. 推荐统计表（recommendation_statistics）
-- ====================================
CREATE TABLE recommendation_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID，主键',
    stat_date DATE NOT NULL COMMENT '统计日期',
    total_recommendations INT DEFAULT 0 COMMENT '总推荐次数',
    successful_recommendations INT DEFAULT 0 COMMENT '成功推荐次数（有反馈且满意）',
    average_score DECIMAL(5,2) DEFAULT 0.00 COMMENT '平均推荐分数',
    average_execution_time DECIMAL(8,2) DEFAULT 0.00 COMMENT '平均执行时间（毫秒）',
    most_popular_drink_id BIGINT COMMENT '最受欢迎酒品ID',
    most_used_rule_id BIGINT COMMENT '最常用规则ID',
    unique_users INT DEFAULT 0 COMMENT '独立用户数（基于IP）',
    feedback_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '反馈率（%）',
    satisfaction_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '满意度（%）',
    algorithm_version VARCHAR(20) DEFAULT 'v1.0' COMMENT '算法版本',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_stat_date_version (stat_date, algorithm_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐统计表';

-- 推荐统计表索引
CREATE INDEX idx_statistics_date ON recommendation_statistics(stat_date);
CREATE INDEX idx_statistics_version ON recommendation_statistics(algorithm_version);
CREATE INDEX idx_statistics_satisfaction ON recommendation_statistics(satisfaction_rate);

-- ====================================
-- 插入基础测试数据
-- ====================================

-- 插入测试问题
INSERT INTO question (title, description, question_type, sort_order) VALUES
('您现在的心情如何？', '选择最符合您当前状态的选项', 'single', 1),
('您今天的场合是？', '告诉我们您在什么场合下饮酒', 'single', 2),
('您偏好的口味类型？', '可以选择多个您喜欢的口味', 'multiple', 3),
('您对酒精度的要求？', '选择您能接受的酒精度范围', 'single', 4),
('您的预算范围？', '选择您可以接受的价格区间', 'single', 5);

-- 插入测试选项（问题1：心情）
INSERT INTO `option` (question_id, content, weight_value, tag_keywords, sort_order) VALUES
(1, '放松休闲', 1.0, '清爽,果味,低度', 1),
(1, '兴奋激动', 1.5, '烈酒,高度,刺激', 2),
(1, '需要解压', 0.8, '温和,舒缓,中度', 3),
(1, '庆祝开心', 1.8, '香槟,气泡,节庆', 4),
(1, '浪漫约会', 1.2, '红酒,优雅,浪漫', 5);

-- 插入测试选项（问题2：场合）
INSERT INTO `option` (question_id, content, weight_value, tag_keywords, sort_order) VALUES
(2, '独自一人', 0.9, '简单,经典,舒适', 1),
(2, '朋友聚会', 1.4, '分享,热闹,互动', 2),
(2, '商务应酬', 1.1, '正式,高端,稳重', 3),
(2, '生日庆祝', 1.7, '庆祝,特别,纪念', 4),
(2, '约会晚餐', 1.3, '浪漫,优雅,精致', 5);

-- 插入测试选项（问题3：口味偏好）
INSERT INTO `option` (question_id, content, weight_value, tag_keywords, sort_order) VALUES
(3, '清爽果味', 1.2, '果味,清爽,甜美', 1),
(3, '浓郁醇厚', 1.5, '浓郁,醇厚,深度', 2),
(3, '酸甜平衡', 1.0, '平衡,酸甜,和谐', 3),
(3, '苦味回甘', 1.3, '苦味,回甘,层次', 4),
(3, '香料调味', 1.4, '香料,复杂,独特', 5);

-- 插入测试选项（问题4：酒精度）
INSERT INTO `option` (question_id, content, weight_value, tag_keywords, sort_order) VALUES
(4, '低度(5-15%)', 0.8, '低度,温和,易饮', 1),
(4, '中度(15-25%)', 1.0, '中度,平衡,适中', 2),
(4, '高度(25-40%)', 1.5, '高度,烈性,刺激', 3),
(4, '超高度(40%+)', 1.8, '超高度,极烈,挑战', 4);

-- 插入测试选项（问题5：预算）
INSERT INTO `option` (question_id, content, weight_value, tag_keywords, sort_order) VALUES
(5, '经济实惠(50元以下)', 0.8, '经济,实惠,性价比', 1),
(5, '中等价位(50-100元)', 1.0, '中等,合理,品质', 2),
(5, '高端选择(100-200元)', 1.3, '高端,精品,奢华', 3),
(5, '顶级体验(200元以上)', 1.5, '顶级,珍藏,极致', 4);

-- 插入测试推荐规则
INSERT INTO recommendation_rule (rule_name, rule_description, option_combination, target_drink_ids, match_score, recommendation_reason, condition_type, priority_level) VALUES
('放松解压推荐', '适合放松心情的清爽酒品', '[1,2,6,16]', '1,3,5', 85.5, '清爽的果味鸡尾酒能够帮助您放松心情，缓解一天的疲劳', 'partial', 8),
('庆祝聚会推荐', '适合庆祝和聚会的热闹酒品', '[4,7,12,19]', '2,4,6', 92.0, '庆祝时刻需要有仪式感的酒品，香槟和特色鸡尾酒是完美选择', 'partial', 9),
('浪漫约会推荐', '适合浪漫约会的优雅酒品', '[5,10,13,18]', '7,8,9', 88.5, '优雅的红酒和精致鸡尾酒为您的浪漫时光增添情调', 'partial', 7),
('商务应酬推荐', '适合商务场合的稳重酒品', '[3,8,14,17]', '10,11,12', 87.0, '经典威士忌和干邑白兰地展现您的品味和专业', 'partial', 6),
('独饮放松推荐', '适合独自品味的舒适酒品', '[1,6,11,16]', '13,14,15', 83.0, '简单经典的酒品让您在独处时光中找到内心的宁静', 'partial', 5);

-- ====================================
-- 创建视图和存储过程
-- ====================================

-- 创建推荐问题完整信息视图
CREATE VIEW v_question_with_options AS
SELECT 
    q.id as question_id,
    q.title,
    q.description,
    q.question_type,
    q.sort_order as question_sort,
    q.is_active as question_active,
    o.id as option_id,
    o.content as option_content,
    o.weight_value,
    o.tag_keywords,
    o.sort_order as option_sort,
    o.is_active as option_active
FROM question q
LEFT JOIN `option` o ON q.id = o.question_id
WHERE q.is_active = 1 AND (o.is_active = 1 OR o.is_active IS NULL)
ORDER BY q.sort_order, o.sort_order;

-- 创建推荐统计汇总视图
CREATE VIEW v_recommendation_summary AS
SELECT 
    DATE(created_at) as stat_date,
    COUNT(*) as total_count,
    COUNT(CASE WHEN user_feedback = 1 THEN 1 END) as satisfied_count,
    COUNT(CASE WHEN user_feedback IS NOT NULL THEN 1 END) as feedback_count,
    AVG(total_score) as avg_score,
    AVG(execution_time_ms) as avg_execution_time,
    COUNT(DISTINCT user_ip) as unique_users,
    algorithm_version
FROM recommendation_log
WHERE is_test_data = 0
GROUP BY DATE(created_at), algorithm_version
ORDER BY stat_date DESC;

-- ====================================
-- 权限和安全设置
-- ====================================

-- 为推荐服务相关表添加注释
ALTER TABLE question COMMENT = '推荐问题表 - 存储推荐流程中的问题信息';
ALTER TABLE `option` COMMENT = '推荐选项表 - 存储问题对应的选项和权重信息';
ALTER TABLE recommendation_rule COMMENT = '推荐规则表 - 存储推荐算法的规则配置';
ALTER TABLE recommendation_log COMMENT = '推荐日志表 - 记录每次推荐的详细信息';
ALTER TABLE recommendation_feedback COMMENT = '推荐反馈表 - 存储用户对推荐结果的反馈';
ALTER TABLE recommendation_statistics COMMENT = '推荐统计表 - 存储推荐服务的统计数据';

-- 脚本执行完成提示
SELECT 'WildNest推荐服务数据库表创建完成！' as message,
       '已创建6个表：question, option, recommendation_rule, recommendation_log, recommendation_feedback, recommendation_statistics' as tables_created,
       '已插入基础测试数据，包括5个问题和对应选项，以及5个推荐规则' as test_data,
       '已创建相关索引、视图和约束，推荐服务数据库准备就绪' as status;