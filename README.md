# WildNest 酒吧H5项目

## 项目概述

WildNest是一个面向酒吧用户的移动端H5应用，提供酒单展示、留言互动、个性化酒品推荐等功能，旨在提升用户体验并增强用户与酒吧的互动性。项目采用前后端分离架构，前端基于Vue.js，后端基于Spring Boot开发。

## 功能模块

### 前台功能（用户端）

#### 1. 酒单模块
- 酒品分类展示（按基酒类型分类）
- 酒品详情查看（名称、酒精度、原料、口感描述等）
- 分类筛选与搜索功能
- 酒品收藏功能（本地存储）
- 招牌酒品轮播展示

#### 2. 留言板模块
- 留言分类与标签切换
- 留言列表展示（用户名、头像、内容、点赞数等）
- 互动功能（点赞、回复、删除/编辑）
- 留言提交与图片上传
- 分页加载与敏感词过滤

#### 3. 今天喝什么模块
- 问答式推荐流程（3-5步选择题）
- 基于用户选择的动态推荐算法
- 可视化选择组件与进度展示
- 推荐结果展示（附推荐理由、图片、酒精度提示）

#### 4. 关于酒吧模块
- 酒吧简介（地址、营业时间、联系方式等）
- 团队介绍（调酒师团队、特色服务）
- 环境与活动图片展示

### 后台管理功能

#### 1. 留言板管理
- 留言列表查看与筛选
- 留言置顶、删除、标记敏感
- 留言数据统计

#### 2. 酒单管理
- 酒品增删改查
- 分类管理与排序调整
- 酒品上下架控制

#### 3. 推荐服务管理
- 问题库维护
- 推荐规则配置

## 技术架构

### 前端技术栈
- 框架：Vue 3 + Vue Router + Vuex/Pinia
- UI组件：Vant UI（移动端）+ Element Plus（后台管理）
- 工具链：Webpack/Vite + Axios + ESLint/Prettier
- 适配方案：rem布局 + @media媒体查询

### 后端技术栈
- 核心框架：Spring Boot 3 + Spring Security
- 数据访问：MyBatis-Plus + MySQL
- 文件存储：阿里云OSS
- 日志系统：SLF4J + Logback + AOP

### 数据库设计
- 酒单相关：drink_category（分类表）、drink（酒品表）
- 留言相关：comment（留言表）、reply（回复表）、oss_image（图片表）
- 推荐相关：question（问题表）、option（选项表）、recommendation（推荐规则表）

### 部署架构
- 服务器：Linux/CentOS
- Web服务：Nginx（反向代理、静态资源、HTTPS）
- 应用服务：Tomcat/Jetty（Spring Boot内嵌）
- 容器化：Docker + Docker Compose

## 开发与部署

### 开发环境要求
- Node.js 16+
- JDK 17+
- MySQL 8.0+
- IDE：VS Code（前端）、IntelliJ IDEA（后端）

### 本地开发流程
1. 克隆项目仓库
2. 前端开发：
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
3. 后端开发：
   - 配置application.yml中的数据库连接
   - 运行SpringBoot应用

### 部署流程
1. 前端构建：
   ```bash
   npm run build
   ```
2. 后端打包：
   ```bash
   mvn clean package
   ```
3. Docker部署：
   ```bash
   docker-compose up -d
   ```

## 项目迭代计划

### V1.0（基础版）
- 完成所有前台模块
- 实现后台管理核心功能

## 项目维护

- 项目文档：详见`docs`目录
- 任务跟踪：详见`task.md`文件
- 贡献指南：详见`CONTRIBUTING.md`文件

## 许可证

本项目采用MIT许可证，详见LICENSE文件。