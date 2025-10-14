# Flavora 项目任务分配方案

## 项目概述

Flavora 是一个基于 Android + Firebase 的美食分享社交应用。项目采用分层架构设计，包含 Model、DAO、DataSource、Repository、Facade 和 UI 六层。

**总代码量统计：**
- Java 文件：31 个
- 布局 XML 文件：12 个
- 图标资源文件：约 30 个（Material Design Icons）
- 配置文件：Firebase、Gradle、权限等

---

## ⚠️ 重要：开发顺序说明

由于代码之间存在依赖关系，**必须按以下阶段顺序开发**，否则会出现编译错误。

### 📋 依赖关系图
```
Model层 (无依赖)
  ↓
Util层 (依赖Model)
  ↓
DataSource层 + DAO接口 (依赖Model, Util)
  ↓
DAO实现层 (依赖DAO接口, DataSource)
  ↓
Repository层 (依赖DAO)
  ↓
Facade层 (依赖Repository, DAO)
  ↓
UI层 (依赖Facade, Repository)
```

---


## 组员分工总览

### 组员 1：基础架构 + 用户认证模块
**核心职责：** 奠定项目基础，搭建底层架构

**第一阶段（关键）：**
- ✅ `dao/DAO.java` - DAO 基础接口（**其他DAO依赖此文件**）
- ✅ `datasource/FirebaseDataSource.java` - 数据源层（630行，**核心文件**）

**第二阶段：**
- ✅ `dao/UserDAO.java` - 用户数据访问对象

**第三阶段：**
- ✅ `repository/AuthRepository.java` - 认证仓库
- ✅ `repository/StorageRepository.java` - 存储仓库

**第四阶段：**
- ✅ `ui/auth/LoginActivity.java` - 登录页面
- ✅ `ui/auth/RegisterActivity.java` - 注册页面
- ✅ `res/layout/activity_login.xml` - 登录布局
- ✅ `res/layout/activity_register.xml` - 注册布局

**UI资源：**
- `ic_person_24.xml`, `ic_close_24.xml` 等

**Firebase配置：**
- Firebase Authentication 配置和测试

**工作量：** 约 1400 行代码 + 2 个界面 + Firebase 配置
**难度：** ⭐⭐⭐⭐⭐（最高，需要先完成底层）

---

### 组员 2：帖子核心模块
**核心职责：** 帖子创建、展示、存储

**第二阶段：**
- ✅ `dao/PostDAO.java` - 帖子数据访问对象

**第三阶段：**
- ✅ `facade/PostFacade.java` - 帖子业务逻辑门面（依赖 PostDAO, UserDAO, StorageRepository）

**第四阶段：**
- ✅ `ui/post/CreatePostBottomSheet.java` - 创建帖子底部表单
- ✅ `ui/post/SelectedImagesAdapter.java` - 已选图片适配器
- ✅ `ui/post/PostDetailActivity.java` - 帖子详情页
- ✅ `res/layout/bottom_sheet_create_post.xml` - 创建帖子布局
- ✅ `res/layout/activity_post_detail.xml` - 帖子详情布局
- ✅ `res/layout/item_selected_image.xml` - 图片项布局
- ✅ `res/layout/dialog_rating.xml` - 评分对话框

**UI资源：**
- `ic_image_24.xml`, `ic_add_24.xml`, `ic_note_add_24.xml` 等

**Firebase配置：**
- Firebase Storage 图片上传测试

**工作量：** 约 1300 行代码 + 4 个界面 + Storage 配置
**难度：** ⭐⭐⭐⭐（需要处理图片上传和复杂表单）

---

### 组员 3：发现页面与帖子展示
**核心职责：** 帖子展示适配器和发现页面

**第二阶段：**
- ✅ `dao/LikeDAO.java` - 点赞数据访问对象

**第四阶段（优先）：**
- ✅ `ui/discover/PostsAdapter.java` - 帖子列表适配器（**重要！多处使用**）
- ✅ `ui/discover/PostImageAdapter.java` - 图片轮播适配器
- ✅ `res/layout/item_post.xml` - 帖子项布局（复杂布局）

**第四阶段：**
- ✅ `ui/discover/DiscoverFragment.java` - 发现页面
- ✅ `res/layout/fragment_discover.xml` - 发现页面布局

**UI资源：**
- `ic_search_24.xml`, `ic_explore_24.xml`, `ic_star_rate_24.xml` 等

**功能实现：**
- ViewPager2 图片轮播和指示器
- 实时搜索过滤（标题/描述/用户名）
- 下拉刷新
- 点赞/收藏交互

**工作量：** 约 1100 行代码 + 2 个界面
**难度：** ⭐⭐⭐⭐（复杂的适配器和ViewPager2）

---

### 组员 4：互动功能模块
**核心职责：** 点赞、收藏逻辑和我的列表页面

**第二阶段：**
- ✅ `dao/FavoriteDAO.java` - 收藏数据访问对象

**第三阶段：**
- ✅ `facade/PostInteractionFacade.java` - 互动业务逻辑门面（依赖 LikeDAO, FavoriteDAO）

**第四阶段：**
- ✅ `ui/profile/MyPostsFragment.java` - 我的帖子列表
- ✅ `ui/profile/MyFavoritesFragment.java` - 我的收藏列表
- ✅ `res/layout/fragment_post_list.xml` - 通用帖子列表布局

**UI资源：**
- `ic_thumb_up_24.xml`, `ic_thumb_up_border_24.xml` - 点赞图标
- `ic_bookmark_24.xml`, `ic_bookmark_border_24.xml` - 收藏图标
- `ic_favorite_24.xml`, `ic_favorite_border_24.xml` - 喜欢图标

**功能实现：**
- 点赞/收藏状态切换
- 原子操作确保数据一致性
- 删除帖子功能（带确认对话框）

**工作量：** 约 1000 行代码 + 1 个界面
**难度：** ⭐⭐⭐（逻辑相对简单，但要注意原子操作）

---

### 组员 5：项目初始化与主框架
**核心职责：** 项目搭建、主框架、个人中心

**第一阶段（关键）：**
- ✅ 项目初始化和 Gradle 配置
  - `build.gradle.kts` (项目级) - Firebase 插件配置
  - `app/build.gradle.kts` (应用级) - 依赖配置、ViewBinding 启用
- ✅ Firebase 项目创建和 `google-services.json` 配置
- ✅ `AndroidManifest.xml` - 配置权限和注册所有 Activity
  - INTERNET 权限
  - ACCESS_NETWORK_STATE 权限
  - READ_MEDIA_IMAGES 权限
  - MainActivity (LAUNCHER)
  - LoginActivity, RegisterActivity, PostDetailActivity
- ✅ `model/HasUUID.java` - UUID 接口
- ✅ `model/User.java` - 用户数据模型
- ✅ `model/Post.java` - 帖子数据模型
- ✅ `model/Like.java` - 点赞数据模型
- ✅ `model/Favorite.java` - 收藏数据模型
- ✅ `util/IdGenerator.java` - ID 生成工具
- ✅ `util/FirestoreFields.java` - Firestore 字段常量
- ✅ 主题和资源配置（`colors.xml`, `themes.xml`, `strings.xml`, `dimens.xml`）

**第四阶段（优先）：**
- ✅ `MainActivity.java` - 主活动（底部导航，**其他Fragment依赖**）
- ✅ `res/layout/activity_main.xml` - 主活动布局
- ✅ `res/navigation/mobile_navigation.xml` - 导航图
- ✅ `res/menu/bottom_nav_menu.xml` - 底部菜单

**第四阶段：**
- ✅ `ui/profile/ProfileFragment.java` - 个人中心页面
- ✅ `ui/profile/ProfilePagerAdapter.java` - ViewPager 适配器
- ✅ `ui/profile/SettingsBottomSheet.java` - 设置表单
- ✅ `res/layout/fragment_profile.xml` - 个人中心布局
- ✅ `res/layout/bottom_sheet_settings.xml` - 设置表单布局

**UI资源（全部）：**
- App Icon 设计和所有尺寸配置
- 导航图标：`ic_home_black_24dp.xml`, `ic_dashboard_black_24dp.xml` 等
- 所有 Material Icons 资源文件（30+）
- 主题配置（浅色 + 深色）

**工作量：** 约 900 行代码 + 3 个界面 + 项目初始化 + 所有图标资源
**难度：** ⭐⭐⭐⭐（需要统筹全局，配置复杂）

---

## 📅 3天紧凑开发计划

**⚠️ 重要：项目只有3天时间，必须高效协作，严格按顺序执行！**

### Day 1（第一天）：基础架构 + DAO层
**上午（组员1+5先行，其他人准备环境）**

| 时间段 | 组员 5 | 组员 1 | 组员 2/3/4 |
|--------|--------|--------|------------|
| 09:00-12:00 | 配置文件（build.gradle.kts、app/build.gradle.kts、AndroidManifest.xml、google-services.json）+ Model层（5个文件）+ Util层（2个文件）+ 资源配置 | DAO.java接口 + FirebaseDataSource.java（630行） | 配置开发环境、学习Firebase |

**下午（并行开发DAO层）**

| 时间段 | 组员 1 | 组员 2 | 组员 3 | 组员 4 | 组员 5 |
|--------|--------|--------|--------|--------|--------|
| 13:00-18:00 | UserDAO + AuthRepository | PostDAO | LikeDAO | FavoriteDAO | MainActivity框架 + 所有图标资源 |

**晚上（可选加班）**

| 时间段 | 组员 1 | 组员 2 | 组员 3 | 组员 4 | 组员 5 |
|--------|--------|--------|--------|--------|--------|
| 19:00-21:00 | StorageRepository | 开始PostFacade | 准备UI布局 | 开始PostInteractionFacade | 导航配置 |

**Day 1 验收：** Model+Util+DAO完成 + FirebaseDataSource完成 + 项目可编译

---

### Day 2（第二天）：Facade层 + 认证UI + 核心适配器
**上午（完成业务逻辑层）**

| 时间段 | 组员 1 | 组员 2 | 组员 3 | 组员 4 | 组员 5 |
|--------|--------|--------|--------|--------|--------|
| 09:00-12:00 | LoginActivity + 布局 | PostFacade（完成） | PostsAdapter（关键！） | PostInteractionFacade（完成） | MainActivity（完成）+ 导航 |

**下午（并行开发UI）**

| 时间段 | 组员 1 | 组员 2 | 组员 3 | 组员 4 | 组员 5 |
|--------|--------|--------|--------|--------|--------|
| 13:00-18:00 | RegisterActivity + 布局 | CreatePostBottomSheet + 布局 | PostImageAdapter + item_post布局 | MyPostsFragment | ProfileFragment + 布局 |

**晚上（继续开发）**

| 时间段 | 组员 1 | 组员 2 | 组员 3 | 组员 4 | 组员 5 |
|--------|--------|--------|--------|--------|--------|
| 19:00-21:00 | 测试登录注册流程 | SelectedImagesAdapter | DiscoverFragment + 布局 | MyFavoritesFragment + 布局 | ProfilePagerAdapter + SettingsBottomSheet |

**Day 2 验收：** 可以登录注册 + 主界面框架完成 + PostsAdapter完成 + 关键页面完成

---

### Day 3（第三天）：完善功能 + 测试 + 修复
**上午（完成剩余UI）**

| 时间段 | 组员 1 | 组员 2 | 组员 3 | 组员 4 | 组员 5 |
|--------|--------|--------|--------|--------|--------|
| 09:00-12:00 | 帮助测试和修bug | PostDetailActivity + 布局 | 完善DiscoverFragment搜索功能 | 完善删除功能 | 完善个人中心显示 |

**下午（集成测试 + bug修复）**

| 时间段 | 所有组员 |
|--------|----------|
| 13:00-18:00 | **全员集成测试**：完整流程测试、修复bug、优化UI |

**晚上（最终验收）**

| 时间段 | 所有组员 |
|--------|----------|
| 19:00-21:00 | 最终测试、打包APK、准备演示 |

**Day 3 验收：** 所有功能可用 + APK可运行 + 无严重bug

---

## 📋 文件创建检查清单

### ✅ Day 1 必须完成清单
```
配置文件：
□ build.gradle.kts (项目级) - Firebase 插件
□ app/build.gradle.kts - 依赖和 ViewBinding
□ google-services.json 配置
□ AndroidManifest.xml - 权限和 Activity 注册

Model 层：
□ model/HasUUID.java
□ model/User.java
□ model/Post.java
□ model/Like.java
□ model/Favorite.java

Util 层：
□ util/IdGenerator.java
□ util/FirestoreFields.java

DAO 层：
□ dao/DAO.java (接口)
□ datasource/FirebaseDataSource.java (630行)
□ dao/UserDAO.java
□ dao/PostDAO.java
□ dao/LikeDAO.java
□ dao/FavoriteDAO.java

Repository 层：
□ repository/AuthRepository.java
□ repository/StorageRepository.java (开始)

UI 资源：
□ res/values/colors.xml
□ res/values/themes.xml
□ res/values/strings.xml
□ res/values/dimens.xml
□ 所有 Material Icons 资源 (30+个)

UI 框架：
□ MainActivity.java (框架)
□ res/layout/activity_main.xml (框架)
```

### ✅ Day 2 必须完成清单
```
□ repository/StorageRepository.java (完成)
□ facade/PostFacade.java
□ facade/PostInteractionFacade.java
□ ui/auth/LoginActivity.java + layout
□ ui/auth/RegisterActivity.java + layout
□ MainActivity.java + layout (完成)
□ navigation/mobile_navigation.xml
□ menu/bottom_nav_menu.xml
□ ui/discover/PostsAdapter.java (关键！)
□ ui/discover/PostImageAdapter.java
□ layout/item_post.xml
□ ui/post/CreatePostBottomSheet.java + layout
□ ui/post/SelectedImagesAdapter.java + layout
□ ui/discover/DiscoverFragment.java + layout
□ ui/profile/ProfileFragment.java + layout
□ ui/profile/ProfilePagerAdapter.java
□ ui/profile/SettingsBottomSheet.java + layout
□ ui/profile/MyPostsFragment.java
□ ui/profile/MyFavoritesFragment.java
□ layout/fragment_post_list.xml
```

### ✅ Day 3 必须完成清单
```
□ ui/post/PostDetailActivity.java + layout
□ layout/dialog_rating.xml
□ 所有功能测试通过
□ bug 修复
□ UI 优化
□ 打包 APK
```

---

## ⚠️ 关键依赖关系提醒

### 🔴 Day 1 上午（组员1+5必须先完成！）
- `DAO.java` 接口 → 所有 DAO 实现依赖此接口
- `FirebaseDataSource.java` → 所有 DAO 调用此类
- `Model` 层 → 所有层都要用到数据模型

### 🟠 Day 1 下午（所有DAO必须完成！）
- `UserDAO` + `PostDAO` → Day 2 的 `PostFacade` 依赖
- `LikeDAO` + `FavoriteDAO` → Day 2 的 `PostInteractionFacade` 依赖

### 🟡 Day 2 上午（Repository/Facade必须完成！）
- `AuthRepository` → 登录注册页面依赖
- `PostFacade` → 创建帖子、发现页面依赖
- `PostInteractionFacade` → 点赞收藏功能依赖

### 🟢 Day 2 上午（UI框架必须先完成！）
- `MainActivity` → 所有 Fragment 挂载点
- `LoginActivity` → 其他功能需要登录
- `PostsAdapter` → 多个页面使用此适配器

---

## 代码规范

1. **命名规范：**
   - 类名：大驼峰（PascalCase）
   - 方法名：小驼峰（camelCase）
   - 常量：全大写下划线分隔（UPPER_SNAKE_CASE）

2. **注释规范：**
   - 所有类、方法必须有中文 JavaDoc
   - 包含 @param、@return、@author 等标签

3. **架构规范：**
   - 严格遵循分层架构，不跨层调用
   - 单例类必须使用双重检查锁定（DCL）
   - Fragment 必须处理生命周期检查

4. **UI规范：**
   - 使用 ViewBinding 访问视图
   - onDestroyView() 必须清理引用
   - 支持深色模式

---

**注意事项：**
- 不要提交敏感信息（API Key、密码等）
- `google-services.json` 已加入 `.gitignore`
- 定期备份代码
