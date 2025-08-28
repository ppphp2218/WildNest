# WildNest 酒吧H5项目 - 后端服务

## 项目简介

WildNest 是一个现代化的酒吧H5项目后端服务，基于 Spring Boot 3 构建，为酒吧提供酒单管理、留言板、酒品推荐等功能。

## 技术栈

- **框架**: Spring Boot 3.5.5
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0+ + MyBatis-Plus
- **连接池**: Druid
- **文件存储**: 阿里云 OSS
- **缓存**: Redis (可选)
- **日志**: SLF4J + Logback
- **API文档**: SpringDoc OpenAPI 3
- **构建工具**: Maven

## 项目结构

```
src/main/java/com/HuangYe/WildNest/
├── aspect/          # AOP切面
├── common/          # 通用类
├── config/          # 配置类
├── controller/      # 控制器
├── entity/          # 实体类
├── exception/       # 异常处理
├── mapper/          # 数据访问层
├── security/        # 安全相关
├── service/         # 服务层
├── util/            # 工具类
└── WildNestApplication.java  # 启动类
```

## 核心功能模块

### 1. 酒单管理
- 酒品分类管理
- 酒品信息管理（CRUD）
- 酒品搜索和筛选
- 图片上传和管理

### 2. 留言板系统
- 用户留言发布
- 留言分类和置顶
- 点赞和回复功能
- 敏感词过滤
- 图片上传支持

### 3. 推荐服务
- 问答式推荐流程
- 推荐算法配置
- 个性化酒品推荐

### 4. 后台管理
- 管理员认证（JWT）
- 留言管理
- 酒单管理
- 推荐规则配置

## 配置说明

### 数据库配置

**开发环境特点：**
- ✅ 使用远程MySQL开发数据库（pppnas.top:9009）
- ✅ 已配置好数据库连接信息
- ✅ 无需配置Redis
- ✅ 使用Druid连接池和监控

```yaml
spring:
  datasource:
    url: jdbc:mysql://pppnas.top:9009/wildnest_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: wildnest_user
    password: wildnest_password
```

### 阿里云OSS配置

```yaml
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
```

### Redis配置（可选）

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+ (可选)

### 2. 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE wildnest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本：
```bash
mysql -u root -p wildnest < ../init_database.sql
```

### 3. 配置文件修改

修改 `src/main/resources/application.yml` 中的配置：
- 数据库连接信息
- 阿里云OSS配置
- Redis配置（如果使用）

### 4. 启动项目

```bash
# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

### 5. 访问地址

- **API服务**: http://localhost:8080/api
- **API文档**: http://localhost:8080/api/swagger-ui.html
- **数据库监控**: http://localhost:8080/api/druid/index.html
- **健康检查**: http://localhost:8080/api/health/check

## API接口说明

### 公开接口
- `/api/health/**` - 健康检查
- `/api/drinks/**` - 酒单查询
- `/api/comments/**` - 留言板
- `/api/recommend/**` - 推荐服务
- `/api/auth/**` - 认证相关

### 管理员接口
- `/api/admin/**` - 后台管理（需要JWT认证）

### 默认管理员账户
- 用户名：`admin`
- 密码：`admin123`

## 开发指南

### 1. 代码规范
- 使用 Lombok 简化代码
- 统一使用 Result 包装响应结果
- 异常统一通过 GlobalExceptionHandler 处理
- 所有 Controller 方法都会被 AOP 记录日志

### 2. 数据库操作
- 使用 MyBatis-Plus 进行数据库操作
- 实体类继承 BaseEntity 获得通用字段
- 支持逻辑删除和自动填充

### 3. 文件上传
- 支持图片上传到阿里云OSS
- 自动生成唯一文件名
- 支持文件类型和大小验证

### 4. 安全机制
- JWT Token 认证
- 密码 BCrypt 加密
- CORS 跨域支持
- 请求日志记录

## 部署说明

### 1. 打包项目

```bash
mvn clean package -DskipTests
```

### 2. 运行JAR包

```bash
java -jar target/WildNest-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3. Docker部署（可选）

```dockerfile
FROM openjdk:17-jre-slim
COPY target/WildNest-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 监控和日志

### 1. 日志文件
- 应用日志：`logs/wildnest.{date}.log`
- 错误日志：`logs/error.{date}.log`
- SQL日志：`logs/sql.{date}.log`

### 2. 监控端点
- Druid监控：`/druid/index.html`
- 健康检查：`/health/check`

## 常见问题

### 1. 数据库连接失败
- 检查数据库服务是否启动
- 确认数据库连接配置正确
- 检查数据库用户权限

### 2. OSS上传失败
- 确认阿里云OSS配置正确
- 检查AccessKey权限
- 确认Bucket存在且可访问

### 3. JWT认证失败
- 检查Token是否过期
- 确认请求头格式：`Authorization: Bearer {token}`

## 联系方式

- 作者：HuangYe
- 项目地址：[WildNest](https://github.com/your-repo/wildnest)
- 问题反馈：[Issues](https://github.com/your-repo/wildnest/issues)

## 许可证

MIT License