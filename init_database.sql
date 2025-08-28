-- =====================================================
-- WildNest 酒吧H5项目数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 创建日期: 2024年
-- =====================================================

-- 创建数据库
-- CREATE DATABASE IF NOT EXISTS `wildnest_db` 
-- DEFAULT CHARACTER SET utf8mb4 
-- DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `wildnest_db`;

-- =====================================================
-- 1. 酒单模块相关表
-- =====================================================

-- 1.1 酒品分类表
CREATE TABLE `drink_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` TEXT NULL COMMENT '分类描述',
  `parent_id` BIGINT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越小越靠前',
  `icon_url` VARCHAR(255) NULL COMMENT '分类图标URL',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_parent_status` (`parent_id`, `status`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒品分类表';

-- 1.2 酒品信息表
CREATE TABLE `drink` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '酒品ID，主键',
  `name` VARCHAR(100) NOT NULL COMMENT '酒品名称',
  `english_name` VARCHAR(100) NULL COMMENT '英文名称',
  `category_id` BIGINT NOT NULL COMMENT '分类ID，外键关联drink_category.id',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `alcohol_content` DECIMAL(4,2) NULL COMMENT '酒精度数(%)',
  `description` TEXT NULL COMMENT '酒品描述',
  `ingredients` TEXT NULL COMMENT '原料成分',
  `taste_notes` TEXT NULL COMMENT '口感描述',
  `image_url` VARCHAR(255) NULL COMMENT '主图片URL',
  `gallery_urls` JSON NULL COMMENT '图片集合(JSON数组)',
  `tags` VARCHAR(255) NULL COMMENT '标签，逗号分隔',
  `is_featured` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否推荐：1-是，0-否',
  `is_available` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否可售：1-是，0-否',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_category_status` (`category_id`, `is_available`),
  INDEX `idx_featured_price` (`is_featured`, `price`),
  INDEX `idx_price` (`price`),
  CONSTRAINT `fk_drink_category` FOREIGN KEY (`category_id`) REFERENCES `drink_category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒品信息表';

-- =====================================================
-- 2. 留言板模块相关表
-- =====================================================

-- 2.1 留言表
CREATE TABLE `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '留言ID，主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(255) NULL COMMENT '头像URL',
  `content` TEXT NOT NULL COMMENT '留言内容',
  `category` VARCHAR(20) NOT NULL DEFAULT 'default' COMMENT '留言分类：default-默认，hot-热门，latest-最新',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
  `is_pinned` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶：1-是，0-否',
  `is_sensitive` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否敏感：1-是，0-否',
  `user_ip` VARCHAR(45) NULL COMMENT '用户IP地址',
  `user_agent` TEXT NULL COMMENT '用户代理信息',
  `device_id` VARCHAR(64) NULL COMMENT '设备ID（用于防重复点赞）',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-删除，2-审核中',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_category_status_time` (`category`, `status`, `created_at`),
  INDEX `idx_pinned_like` (`is_pinned`, `like_count`),
  INDEX `idx_device_ip` (`device_id`, `user_ip`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 2.2 回复表
CREATE TABLE `reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID，主键',
  `comment_id` BIGINT NOT NULL COMMENT '留言ID，外键关联comment.id',
  `parent_id` BIGINT NULL DEFAULT 0 COMMENT '父回复ID，0表示直接回复留言',
  `username` VARCHAR(50) NOT NULL COMMENT '回复者昵称',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `user_ip` VARCHAR(45) NULL COMMENT '用户IP地址',
  `device_id` VARCHAR(64) NULL COMMENT '设备ID',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-删除',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_comment_status` (`comment_id`, `status`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_reply_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回复表';

-- 2.3 OSS图片表
CREATE TABLE `oss_image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID，主键',
  `comment_id` BIGINT NULL COMMENT '关联留言ID，外键关联comment.id',
  `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_name` VARCHAR(255) NOT NULL COMMENT '存储文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
  `file_url` VARCHAR(500) NOT NULL COMMENT '访问URL',
  `thumbnail_url` VARCHAR(500) NULL COMMENT '缩略图URL',
  `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
  `file_type` VARCHAR(20) NOT NULL COMMENT '文件类型(jpg,png,gif等)',
  `width` INT NULL COMMENT '图片宽度',
  `height` INT NULL COMMENT '图片高度',
  `upload_ip` VARCHAR(45) NULL COMMENT '上传IP',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-删除',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_comment_id` (`comment_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_image_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OSS图片表';

-- 2.4 点赞记录表
CREATE TABLE `like_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
  `comment_id` BIGINT NOT NULL COMMENT '留言ID，外键关联comment.id',
  `user_ip` VARCHAR(45) NULL COMMENT '用户IP地址',
  `device_id` VARCHAR(64) NULL COMMENT '设备ID',
  `user_fingerprint` VARCHAR(128) NULL COMMENT '用户指纹（浏览器特征）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_comment_device` (`comment_id`, `device_id`),
  INDEX `idx_comment_id` (`comment_id`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_like_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- =====================================================
-- 3. 推荐服务模块相关表
-- =====================================================

-- 3.1 问题表
CREATE TABLE `question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问题ID，主键',
  `title` VARCHAR(200) NOT NULL COMMENT '问题标题',
  `description` TEXT NULL COMMENT '问题描述',
  `question_type` VARCHAR(20) NOT NULL DEFAULT 'single' COMMENT '问题类型：single-单选，multiple-多选',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，决定问题出现顺序',
  `is_required` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否必答：1-是，0-否',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_status` (`sort_order`, `status`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';

-- 3.2 选项表
CREATE TABLE `option` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '选项ID，主键',
  `question_id` BIGINT NOT NULL COMMENT '问题ID，外键关联question.id',
  `content` VARCHAR(200) NOT NULL COMMENT '选项内容',
  `description` TEXT NULL COMMENT '选项描述',
  `weight` DECIMAL(5,2) NOT NULL DEFAULT 1.0 COMMENT '权重值，用于推荐算法',
  `icon_url` VARCHAR(255) NULL COMMENT '选项图标URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_question_order` (`question_id`, `sort_order`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选项表';

-- 3.3 推荐规则表
CREATE TABLE `recommendation_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID，主键',
  `name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `description` TEXT NULL COMMENT '规则描述',
  `option_combination` JSON NOT NULL COMMENT '选项组合(JSON数组，存储option_id)',
  `drink_ids` JSON NOT NULL COMMENT '推荐酒品ID列表(JSON数组)',
  `match_type` VARCHAR(20) NOT NULL DEFAULT 'exact' COMMENT '匹配类型：exact-精确匹配，partial-部分匹配',
  `priority` INT NOT NULL DEFAULT 0 COMMENT '优先级，数值越大优先级越高',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_priority_status` (`priority`, `status`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐规则表';

-- 3.4 推荐记录表
CREATE TABLE `recommendation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
  `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
  `user_selections` JSON NOT NULL COMMENT '用户选择记录(JSON对象)',
  `recommended_drinks` JSON NOT NULL COMMENT '推荐结果(JSON数组)',
  `rule_id` BIGINT NULL COMMENT '匹配的规则ID',
  `user_ip` VARCHAR(45) NULL COMMENT '用户IP',
  `user_agent` TEXT NULL COMMENT '用户代理',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_session_id` (`session_id`),
  INDEX `idx_rule_id` (`rule_id`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_log_rule` FOREIGN KEY (`rule_id`) REFERENCES `recommendation_rule` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐记录表';

-- =====================================================
-- 4. 系统管理相关表
-- =====================================================

-- 4.1 管理员表
CREATE TABLE `admin_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID，主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `salt` VARCHAR(32) NOT NULL COMMENT '密码盐值',
  `real_name` VARCHAR(50) NULL COMMENT '真实姓名',
  `email` VARCHAR(100) NULL COMMENT '邮箱',
  `phone` VARCHAR(20) NULL COMMENT '手机号',
  `avatar_url` VARCHAR(255) NULL COMMENT '头像URL',
  `role` VARCHAR(20) NOT NULL DEFAULT 'admin' COMMENT '角色：admin-管理员，super-超级管理员',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `last_login_at` TIMESTAMP NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(45) NULL COMMENT '最后登录IP',
  `login_count` INT NOT NULL DEFAULT 0 COMMENT '登录次数',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username` (`username`),
  INDEX `idx_role` (`role`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 4.2 系统配置表
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键，唯一',
  `config_value` TEXT NULL COMMENT '配置值',
  `config_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
  `description` VARCHAR(255) NULL COMMENT '配置描述',
  `is_public` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否公开：1-是，0-否',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_config_key` (`config_key`),
  INDEX `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 5. 初始化数据
-- =====================================================

-- 5.1 初始化酒品分类数据
INSERT INTO `drink_category` (`id`, `name`, `description`, `parent_id`, `sort_order`, `status`) VALUES
(1, '威士忌', '威士忌类酒品，包括苏格兰威士忌、美国威士忌等', 0, 1, 1),
(2, '伏特加', '伏特加类酒品，口感纯净清冽', 0, 2, 1),
(3, '朗姆酒', '朗姆酒类酒品，热带风情浓郁', 0, 3, 1),
(4, '金酒', '金酒类酒品，植物香料丰富', 0, 4, 1),
(5, '龙舌兰', '龙舌兰酒类，墨西哥特色烈酒', 0, 5, 1),
(6, '果味酒', '果味调制酒品，口感清甜', 0, 6, 1),
(7, '经典鸡尾酒', '经典调制鸡尾酒系列', 0, 7, 1),
(8, '创意特调', '酒吧原创特色调制酒品', 0, 8, 1);

-- 5.2 初始化酒品数据
INSERT INTO `drink` (`name`, `english_name`, `category_id`, `price`, `alcohol_content`, `description`, `ingredients`, `taste_notes`, `tags`, `is_featured`, `is_available`, `sort_order`) VALUES
('长岛冰茶', 'Long Island Iced Tea', 7, 68.00, 22.00, '经典长岛冰茶，多种烈酒调制，口感浓烈', '伏特加、朗姆酒、金酒、龙舌兰、柠檬汁、可乐', '口感浓烈，层次丰富，微甜带酸', '经典,烈酒,解压', 1, 1, 1),
('莫吉托', 'Mojito', 7, 58.00, 12.00, '古巴经典鸡尾酒，清新薄荷香', '白朗姆酒、薄荷叶、青柠汁、苏打水、糖浆', '清新薄荷香，酸甜平衡，口感清爽', '清爽,薄荷,夏日', 1, 1, 2),
('玛格丽特', 'Margarita', 7, 65.00, 18.00, '墨西哥经典鸡尾酒，酸甜平衡', '龙舌兰酒、橙皮酒、青柠汁、盐边', '酸甜平衡，龙舌兰香味突出，盐边提味', '经典,酸甜,墨西哥', 1, 1, 3),
('血腥玛丽', 'Bloody Mary', 7, 62.00, 15.00, '经典早餐鸡尾酒，番茄汁调制', '伏特加、番茄汁、柠檬汁、辣椒酱、芹菜盐', '咸鲜微辣，番茄香浓，口感厚重', '早餐酒,咸鲜,微辣', 0, 1, 4),
('蓝色夏威夷', 'Blue Hawaii', 6, 72.00, 14.00, '热带风情果味鸡尾酒，颜值很高', '白朗姆酒、蓝橙酒、椰浆、菠萝汁', '热带果香浓郁，口感甜美，颜值极高', '果味,热带,颜值', 1, 1, 5),
('威士忌酸', 'Whiskey Sour', 1, 75.00, 20.00, '威士忌经典调制，酸甜平衡', '威士忌、柠檬汁、糖浆、蛋白', '威士忌香醇，酸甜平衡，口感顺滑', '威士忌,酸甜,经典', 0, 1, 6),
('金汤力', 'Gin & Tonic', 4, 55.00, 12.00, '简单经典的金酒调制', '金酒、汤力水、青柠片', '金酒植物香，汤力水苦甜，清爽怡人', '简单,清爽,经典', 0, 1, 7),
('野莓特调', 'Wild Berry Special', 8, 78.00, 16.00, '酒吧原创野莓风味特调', '伏特加、野莓利口酒、柠檬汁、蔓越莓汁', '野莓香甜，酸甜平衡，口感层次丰富', '原创,野莓,特调', 1, 1, 8);

-- 5.3 初始化推荐问题数据
INSERT INTO `question` (`title`, `description`, `question_type`, `sort_order`, `is_required`, `status`) VALUES
('今天的心情如何？', '选择最符合你当前心情的选项', 'single', 1, 1, 1),
('你更偏爱什么口味？', '选择你喜欢的酒品口味类型', 'single', 2, 1, 1),
('今天是什么场合？', '告诉我们你今天的聚会场合', 'single', 3, 1, 1),
('你的酒量如何？', '选择最符合你酒量的选项', 'single', 4, 1, 1);

-- 5.4 初始化推荐选项数据
INSERT INTO `option` (`question_id`, `content`, `description`, `weight`, `sort_order`, `status`) VALUES
-- 心情问题选项
(1, '需要放松解压', '工作压力大，需要放松一下', 1.5, 1, 1),
(1, '心情愉悦开心', '今天心情很好，想要庆祝', 1.2, 2, 1),
(1, '想要尝试新鲜', '想尝试一些没喝过的酒', 1.0, 3, 1),
(1, '比较平静淡然', '心情平静，随意喝点什么', 0.8, 4, 1),
-- 口味问题选项
(2, '喜欢酸甜口感', '偏爱酸甜平衡的口味', 1.3, 1, 1),
(2, '偏爱清爽口感', '喜欢清爽怡人的感觉', 1.2, 2, 1),
(2, '喜欢浓烈口感', '能接受比较烈的酒', 1.5, 3, 1),
(2, '偏爱果香味道', '喜欢有果香的酒品', 1.1, 4, 1),
-- 场合问题选项
(3, '朋友聚会', '和朋友一起聚会喝酒', 1.2, 1, 1),
(3, '独自小酌', '一个人安静地喝点酒', 1.0, 2, 1),
(3, '庆祝生日', '今天是生日或其他庆祝', 1.4, 3, 1),
(3, '约会浪漫', '和恋人约会的浪漫时光', 1.3, 4, 1),
-- 酒量问题选项
(4, '酒量一般', '平时很少喝酒，酒量一般', 0.8, 1, 1),
(4, '酒量不错', '经常喝酒，酒量还可以', 1.2, 2, 1),
(4, '千杯不醉', '酒量很好，能喝很多', 1.5, 3, 1),
(4, '浅尝辄止', '只想稍微喝一点点', 0.6, 4, 1);

-- 5.5 初始化推荐规则数据
INSERT INTO `recommendation_rule` (`name`, `description`, `option_combination`, `drink_ids`, `match_type`, `priority`, `status`) VALUES
('解压烈酒组合', '适合需要解压的烈酒推荐', '[1, 7, 9, 14]', '[1, 6]', 'partial', 90, 1),
('庆祝特调组合', '适合庆祝场合的特色酒品', '[2, 5, 11, 13]', '[5, 8]', 'partial', 85, 1),
('清爽果味组合', '清爽果味酒品推荐', '[3, 6, 10, 12]', '[2, 5, 7]', 'partial', 80, 1),
('经典鸡尾酒组合', '经典鸡尾酒推荐', '[2, 5, 9, 13]', '[1, 2, 3]', 'partial', 75, 1),
('新手友好组合', '适合酒量一般的温和酒品', '[4, 8, 10, 16]', '[2, 7, 5]', 'partial', 70, 1);

-- 5.6 初始化管理员数据
-- 默认管理员账号: admin, 密码: admin123 (实际使用时请修改)
-- INSERT INTO `admin_user` (`username`, `password`, `salt`, `real_name`, `role`, `status`) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 'wildnest2024', '系统管理员', 'super', 1);

-- 5.7 初始化系统配置数据
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `is_public`) VALUES
('site_name', 'WildNest 酒吧', 'string', '网站名称', 1),
('site_description', '专业精品酒吧，为您提供优质的酒品和服务', 'string', '网站描述', 1),
('contact_phone', '400-123-4567', 'string', '联系电话', 1),
('contact_address', '北京市朝阳区xxx街道xxx号', 'string', '酒吧地址', 1),
('business_hours', '18:00-02:00', 'string', '营业时间', 1),
('max_comment_length', '500', 'number', '留言最大长度', 0),
('comment_audit_enabled', 'false', 'boolean', '是否开启留言审核', 0),
('sensitive_words', '["政治", "色情", "广告", "赌博"]', 'json', '敏感词列表', 0),
('oss_bucket_name', 'wildnest-images', 'string', 'OSS存储桶名称', 0),
('oss_endpoint', 'oss-cn-beijing.aliyuncs.com', 'string', 'OSS访问端点', 0);

-- =====================================================
-- 6. 创建视图（可选）
-- =====================================================

-- 6.1 酒品详情视图（包含分类信息）
CREATE VIEW `v_drink_detail` AS
SELECT 
    d.id,
    d.name,
    d.english_name,
    d.price,
    d.alcohol_content,
    d.description,
    d.ingredients,
    d.taste_notes,
    d.image_url,
    d.gallery_urls,
    d.tags,
    d.is_featured,
    d.is_available,
    d.view_count,
    d.created_at,
    c.name AS category_name,
    c.description AS category_description
FROM `drink` d
LEFT JOIN `drink_category` c ON d.category_id = c.id
WHERE d.is_available = 1;

-- 6.2 留言统计视图
CREATE VIEW `v_comment_stats` AS
SELECT 
    c.id,
    c.username,
    c.content,
    c.category,
    c.like_count,
    c.reply_count,
    c.is_pinned,
    c.created_at,
    COUNT(r.id) AS actual_reply_count
FROM `comment` c
LEFT JOIN `reply` r ON c.id = r.comment_id AND r.status = 1
WHERE c.status = 1
GROUP BY c.id;

-- =====================================================
-- 7. 创建存储过程（可选）
-- =====================================================

-- 7.1 更新留言回复数的存储过程
DELIMITER //
CREATE PROCEDURE `UpdateCommentReplyCount`(IN comment_id BIGINT)
BEGIN
    UPDATE `comment` 
    SET `reply_count` = (
        SELECT COUNT(*) 
        FROM `reply` 
        WHERE `comment_id` = comment_id AND `status` = 1
    )
    WHERE `id` = comment_id;
END //
DELIMITER ;

-- 7.2 更新留言点赞数的存储过程
DELIMITER //
CREATE PROCEDURE `UpdateCommentLikeCount`(IN comment_id BIGINT)
BEGIN
    UPDATE `comment` 
    SET `like_count` = (
        SELECT COUNT(*) 
        FROM `like_record` 
        WHERE `comment_id` = comment_id
    )
    WHERE `id` = comment_id;
END //
DELIMITER ;

-- =====================================================
-- 8. 数据统计逻辑说明（应用层实现）
-- =====================================================

-- 注意：统计字段需要在应用代码中手动维护，避免数据库权限问题
-- 以下统计字段需要在应用层实现：
-- 1. comment.reply_count - 回复数统计
-- 2. comment.like_count - 点赞数统计
--
-- 应用层实现要点：
-- - 新增回复时：UPDATE comment SET reply_count = reply_count + 1 WHERE id = ?
-- - 删除回复时：UPDATE comment SET reply_count = reply_count - 1 WHERE id = ?
-- - 新增点赞时：UPDATE comment SET like_count = like_count + 1 WHERE id = ?
-- - 删除点赞时：UPDATE comment SET like_count = like_count - 1 WHERE id = ?
-- - 定期校验：使用COUNT查询校正统计数据

-- =====================================================
-- 9. 权限设置（生产环境使用）
-- =====================================================

-- 创建应用数据库用户（请根据实际情况修改用户名和密码）
-- CREATE USER 'wildnest_app'@'%' IDENTIFIED BY 'your_strong_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON wildnest_db.* TO 'wildnest_app'@'%';
-- FLUSH PRIVILEGES;

-- =====================================================
-- 初始化完成
-- =====================================================

-- 显示所有表
SHOW TABLES;

-- 显示数据库信息
SELECT 
    SCHEMA_NAME as '数据库名',
    DEFAULT_CHARACTER_SET_NAME as '字符集',
    DEFAULT_COLLATION_NAME as '排序规则'
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME = 'wildnest_db';

-- 显示表统计信息
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '记录数',
    ROUND(DATA_LENGTH/1024/1024, 2) as '数据大小(MB)',
    TABLE_COMMENT as '表注释'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'wildnest_db'
ORDER BY TABLE_NAME;

-- =====================================================
-- 说明：
-- 1. 请根据实际需求调整配置参数
-- 2. 生产环境请修改默认管理员密码
-- 3. 建议定期备份数据库
-- 4. 可根据业务需求添加更多初始化数据
-- =====================================================