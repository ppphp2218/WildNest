# WildNest 酒吧H5项目数据库设计文档

## 1. 数据库概述

### 1.1 数据库基本信息
- **数据库名称**: wildnest_db
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **存储引擎**: InnoDB
- **数据库版本**: MySQL 8.0+

### 1.2 设计原则
- 遵循第三范式，减少数据冗余
- 合理使用索引提升查询性能
- 预留扩展字段，便于后续功能迭代
- 统一命名规范，提高可维护性
- 考虑数据安全性和完整性约束

## 2. 数据库表结构设计

### 2.1 酒单模块相关表

#### 2.1.1 酒品分类表 (drink_category)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 分类ID，主键 |
| name | VARCHAR | 50 | NOT NULL | - | - | IDX | 分类名称 |
| description | TEXT | - | NULL | - | - | - | 分类描述 |
| parent_id | BIGINT | - | NULL | 0 | - | IDX | 父分类ID，0表示顶级分类 |
| sort_order | INT | - | NOT NULL | 0 | - | - | 排序值，数值越小越靠前 |
| icon_url | VARCHAR | 255 | NULL | - | - | - | 分类图标URL |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-启用，0-禁用 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 酒品分类表，支持多级分类结构，用于组织酒品的层次化展示。

#### 2.1.2 酒品信息表 (drink)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 酒品ID，主键 |
| name | VARCHAR | 100 | NOT NULL | - | - | IDX | 酒品名称 |
| english_name | VARCHAR | 100 | NULL | - | - | - | 英文名称 |
| category_id | BIGINT | - | NOT NULL | - | - | FK,IDX | 分类ID，外键关联drink_category.id |
| price | DECIMAL | 10,2 | NOT NULL | 0.00 | - | IDX | 价格 |
| alcohol_content | DECIMAL | 4,2 | NULL | - | - | - | 酒精度数(%) |
| description | TEXT | - | NULL | - | - | - | 酒品描述 |
| ingredients | TEXT | - | NULL | - | - | - | 原料成分 |
| taste_notes | TEXT | - | NULL | - | - | - | 口感描述 |
| image_url | VARCHAR | 255 | NULL | - | - | - | 主图片URL |
| gallery_urls | JSON | - | NULL | - | - | - | 图片集合(JSON数组) |
| tags | VARCHAR | 255 | NULL | - | - | - | 标签，逗号分隔 |
| is_featured | TINYINT | 1 | NOT NULL | 0 | - | IDX | 是否推荐：1-是，0-否 |
| is_available | TINYINT | 1 | NOT NULL | 1 | - | IDX | 是否可售：1-是，0-否 |
| view_count | INT | - | NOT NULL | 0 | - | - | 浏览次数 |
| sort_order | INT | - | NOT NULL | 0 | - | - | 排序值 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 酒品信息表，存储酒品的详细信息，包括价格、描述、图片等。

### 2.2 留言板模块相关表

#### 2.2.1 留言表 (comment)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 留言ID，主键 |
| username | VARCHAR | 50 | NOT NULL | - | - | IDX | 用户昵称 |
| avatar_url | VARCHAR | 255 | NULL | - | - | - | 头像URL |
| content | TEXT | - | NOT NULL | - | - | - | 留言内容 |
| category | VARCHAR | 20 | NOT NULL | 'default' | - | IDX | 留言分类：default-默认，hot-热门，latest-最新 |
| like_count | INT | - | NOT NULL | 0 | - | IDX | 点赞数 |
| reply_count | INT | - | NOT NULL | 0 | - | - | 回复数 |
| is_pinned | TINYINT | 1 | NOT NULL | 0 | - | IDX | 是否置顶：1-是，0-否 |
| is_sensitive | TINYINT | 1 | NOT NULL | 0 | - | IDX | 是否敏感：1-是，0-否 |
| user_ip | VARCHAR | 45 | NULL | - | - | IDX | 用户IP地址 |
| user_agent | TEXT | - | NULL | - | - | - | 用户代理信息 |
| device_id | VARCHAR | 64 | NULL | - | - | IDX | 设备ID（用于防重复点赞） |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-正常，0-删除，2-审核中 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | IDX | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 留言表，存储用户发布的留言信息，支持分类、置顶、敏感词标记等功能。

#### 2.2.2 回复表 (reply)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 回复ID，主键 |
| comment_id | BIGINT | - | NOT NULL | - | - | FK,IDX | 留言ID，外键关联comment.id |
| parent_id | BIGINT | - | NULL | 0 | - | IDX | 父回复ID，0表示直接回复留言 |
| username | VARCHAR | 50 | NOT NULL | - | - | - | 回复者昵称 |
| content | TEXT | - | NOT NULL | - | - | - | 回复内容 |
| user_ip | VARCHAR | 45 | NULL | - | - | - | 用户IP地址 |
| device_id | VARCHAR | 64 | NULL | - | - | - | 设备ID |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-正常，0-删除 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | IDX | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 回复表，存储对留言的回复信息，支持多级回复结构。

#### 2.2.3 OSS图片表 (oss_image)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 图片ID，主键 |
| comment_id | BIGINT | - | NULL | - | - | FK,IDX | 关联留言ID，外键关联comment.id |
| original_name | VARCHAR | 255 | NOT NULL | - | - | - | 原始文件名 |
| file_name | VARCHAR | 255 | NOT NULL | - | - | - | 存储文件名 |
| file_path | VARCHAR | 500 | NOT NULL | - | - | - | 文件路径 |
| file_url | VARCHAR | 500 | NOT NULL | - | - | - | 访问URL |
| thumbnail_url | VARCHAR | 500 | NULL | - | - | - | 缩略图URL |
| file_size | BIGINT | - | NOT NULL | 0 | - | - | 文件大小(字节) |
| file_type | VARCHAR | 20 | NOT NULL | - | - | - | 文件类型(jpg,png,gif等) |
| width | INT | - | NULL | - | - | - | 图片宽度 |
| height | INT | - | NULL | - | - | - | 图片高度 |
| upload_ip | VARCHAR | 45 | NULL | - | - | - | 上传IP |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-正常，0-删除 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | IDX | 创建时间 |

**表说明**: OSS图片表，存储上传到阿里云OSS的图片信息，关联留言表。

#### 2.2.4 点赞记录表 (like_record)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 记录ID，主键 |
| comment_id | BIGINT | - | NOT NULL | - | - | FK,IDX | 留言ID，外键关联comment.id |
| user_ip | VARCHAR | 45 | NULL | - | - | - | 用户IP地址 |
| device_id | VARCHAR | 64 | NULL | - | - | - | 设备ID |
| user_fingerprint | VARCHAR | 128 | NULL | - | - | - | 用户指纹（浏览器特征） |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | IDX | 点赞时间 |

**表说明**: 点赞记录表，用于防止重复点赞，记录用户的点赞行为。

### 2.3 推荐服务模块相关表

#### 2.3.1 问题表 (question)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 问题ID，主键 |
| title | VARCHAR | 200 | NOT NULL | - | - | - | 问题标题 |
| description | TEXT | - | NULL | - | - | - | 问题描述 |
| question_type | VARCHAR | 20 | NOT NULL | 'single' | - | IDX | 问题类型：single-单选，multiple-多选 |
| sort_order | INT | - | NOT NULL | 0 | - | - | 排序值，决定问题出现顺序 |
| is_required | TINYINT | 1 | NOT NULL | 1 | - | - | 是否必答：1-是，0-否 |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-启用，0-禁用 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 问题表，存储推荐系统的问题信息，用于构建问答式推荐流程。

#### 2.3.2 选项表 (option)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 选项ID，主键 |
| question_id | BIGINT | - | NOT NULL | - | - | FK,IDX | 问题ID，外键关联question.id |
| content | VARCHAR | 200 | NOT NULL | - | - | - | 选项内容 |
| description | TEXT | - | NULL | - | - | - | 选项描述 |
| weight | DECIMAL | 5,2 | NOT NULL | 1.0 | - | - | 权重值，用于推荐算法 |
| icon_url | VARCHAR | 255 | NULL | - | - | - | 选项图标URL |
| sort_order | INT | - | NOT NULL | 0 | - | - | 排序值 |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-启用，0-禁用 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 选项表，存储问题的可选答案，每个选项有对应的权重值用于推荐计算。

#### 2.3.3 推荐规则表 (recommendation_rule)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 规则ID，主键 |
| name | VARCHAR | 100 | NOT NULL | - | - | - | 规则名称 |
| description | TEXT | - | NULL | - | - | - | 规则描述 |
| option_combination | JSON | - | NOT NULL | - | - | - | 选项组合(JSON数组，存储option_id) |
| drink_ids | JSON | - | NOT NULL | - | - | - | 推荐酒品ID列表(JSON数组) |
| match_type | VARCHAR | 20 | NOT NULL | 'exact' | - | - | 匹配类型：exact-精确匹配，partial-部分匹配 |
| priority | INT | - | NOT NULL | 0 | - | IDX | 优先级，数值越大优先级越高 |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-启用，0-禁用 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 推荐规则表，存储推荐算法的规则配置，根据用户选择的选项组合推荐相应的酒品。

#### 2.3.4 推荐记录表 (recommendation_log)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 记录ID，主键 |
| session_id | VARCHAR | 64 | NOT NULL | - | - | IDX | 会话ID |
| user_selections | JSON | - | NOT NULL | - | - | - | 用户选择记录(JSON对象) |
| recommended_drinks | JSON | - | NOT NULL | - | - | - | 推荐结果(JSON数组) |
| rule_id | BIGINT | - | NULL | - | - | FK,IDX | 匹配的规则ID |
| user_ip | VARCHAR | 45 | NULL | - | - | - | 用户IP |
| user_agent | TEXT | - | NULL | - | - | - | 用户代理 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | IDX | 创建时间 |

**表说明**: 推荐记录表，记录用户的推荐使用情况，用于数据分析和算法优化。

### 2.4 系统管理相关表

#### 2.4.1 管理员表 (admin_user)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 管理员ID，主键 |
| username | VARCHAR | 50 | NOT NULL | - | - | UK | 用户名，唯一 |
| password | VARCHAR | 255 | NOT NULL | - | - | - | 密码（加密存储） |
| salt | VARCHAR | 32 | NOT NULL | - | - | - | 密码盐值 |
| real_name | VARCHAR | 50 | NULL | - | - | - | 真实姓名 |
| email | VARCHAR | 100 | NULL | - | - | - | 邮箱 |
| phone | VARCHAR | 20 | NULL | - | - | - | 手机号 |
| avatar_url | VARCHAR | 255 | NULL | - | - | - | 头像URL |
| role | VARCHAR | 20 | NOT NULL | 'admin' | - | IDX | 角色：admin-管理员，super-超级管理员 |
| status | TINYINT | 1 | NOT NULL | 1 | - | IDX | 状态：1-正常，0-禁用 |
| last_login_at | TIMESTAMP | - | NULL | - | - | - | 最后登录时间 |
| last_login_ip | VARCHAR | 45 | NULL | - | - | - | 最后登录IP |
| login_count | INT | - | NOT NULL | 0 | - | - | 登录次数 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 管理员表，存储后台管理系统的用户信息，支持角色权限控制。

#### 2.4.2 系统配置表 (system_config)

| 字段名 | 数据类型 | 长度 | 是否为空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|----------|--------|------|------|------|
| id | BIGINT | - | NOT NULL | AUTO_INCREMENT | PK | - | 配置ID，主键 |
| config_key | VARCHAR | 100 | NOT NULL | - | - | UK | 配置键，唯一 |
| config_value | TEXT | - | NULL | - | - | - | 配置值 |
| config_type | VARCHAR | 20 | NOT NULL | 'string' | - | IDX | 配置类型：string,number,boolean,json |
| description | VARCHAR | 255 | NULL | - | - | - | 配置描述 |
| is_public | TINYINT | 1 | NOT NULL | 0 | - | - | 是否公开：1-是，0-否 |
| created_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | - | - | 更新时间 |

**表说明**: 系统配置表，存储系统的各种配置参数，支持动态配置管理。

## 3. 索引设计

### 3.1 主要索引列表

| 表名 | 索引名 | 索引类型 | 字段 | 说明 |
|------|--------|----------|------|------|
| drink_category | idx_name | NORMAL | name | 分类名称索引 |
| drink_category | idx_parent_status | NORMAL | parent_id, status | 父分类和状态组合索引 |
| drink | idx_name | NORMAL | name | 酒品名称索引 |
| drink | idx_category_status | NORMAL | category_id, is_available | 分类和可售状态组合索引 |
| drink | idx_featured_price | NORMAL | is_featured, price | 推荐和价格组合索引 |
| comment | idx_category_status_time | NORMAL | category, status, created_at | 分类、状态、时间组合索引 |
| comment | idx_pinned_like | NORMAL | is_pinned, like_count | 置顶和点赞数组合索引 |
| comment | idx_device_ip | NORMAL | device_id, user_ip | 设备和IP组合索引 |
| reply | idx_comment_status | NORMAL | comment_id, status | 留言和状态组合索引 |
| like_record | idx_comment_device | UNIQUE | comment_id, device_id | 留言和设备唯一索引（防重复点赞） |
| question | idx_order_status | NORMAL | sort_order, status | 排序和状态组合索引 |
| option | idx_question_order | NORMAL | question_id, sort_order | 问题和排序组合索引 |
| recommendation_rule | idx_priority_status | NORMAL | priority, status | 优先级和状态组合索引 |
| admin_user | uk_username | UNIQUE | username | 用户名唯一索引 |
| system_config | uk_config_key | UNIQUE | config_key | 配置键唯一索引 |

## 4. 外键约束

### 4.1 外键关系列表

| 子表 | 子表字段 | 父表 | 父表字段 | 约束名 | 删除规则 |
|------|----------|------|----------|--------|----------|
| drink | category_id | drink_category | id | fk_drink_category | RESTRICT |
| reply | comment_id | comment | id | fk_reply_comment | CASCADE |
| oss_image | comment_id | comment | id | fk_image_comment | CASCADE |
| like_record | comment_id | comment | id | fk_like_comment | CASCADE |
| option | question_id | question | id | fk_option_question | CASCADE |
| recommendation_rule | rule_id | recommendation_rule | id | fk_log_rule | SET NULL |

## 5. 数据字典

### 5.1 枚举值说明

#### 5.1.1 状态类型
- **通用状态**: 1-启用/正常，0-禁用/删除
- **留言状态**: 1-正常，0-删除，2-审核中
- **管理员角色**: admin-普通管理员，super-超级管理员
- **问题类型**: single-单选，multiple-多选
- **匹配类型**: exact-精确匹配，partial-部分匹配
- **配置类型**: string-字符串，number-数字，boolean-布尔，json-JSON对象

#### 5.1.2 分类标识
- **留言分类**: default-默认，hot-热门，latest-最新

## 6. 性能优化建议

### 6.1 查询优化
1. **分页查询**: 使用LIMIT + OFFSET，避免大偏移量
2. **热点数据**: 对热门留言、推荐酒品等建立缓存
3. **统计查询**: 定期更新统计数据，避免实时计算
4. **全文搜索**: 考虑使用Elasticsearch进行酒品搜索

### 6.2 存储优化
1. **图片存储**: 大图片存储在OSS，数据库只存URL
2. **JSON字段**: 合理使用JSON类型存储复杂数据结构
3. **历史数据**: 定期归档老旧数据，保持表大小合理

### 6.3 安全考虑
1. **敏感数据**: 密码使用加盐哈希，不存储明文
2. **SQL注入**: 使用参数化查询，避免拼接SQL
3. **数据备份**: 定期备份重要数据，制定恢复策略

## 7. 扩展性设计

### 7.1 预留字段
- 各主要表预留了扩展字段，便于后续功能迭代
- JSON字段支持灵活的数据结构扩展

### 7.2 分表分库
- 当数据量增长时，可考虑按时间或用户维度分表
- 留言表和推荐记录表是主要的分表候选

### 7.3 缓存策略
- 酒品信息、分类数据适合Redis缓存
- 热门留言、推荐规则可建立多级缓存

---

**文档版本**: v1.0  
**创建日期**: 2024年  
**维护人员**: 开发团队  
**更新记录**: 初始版本，包含完整的数据库设计