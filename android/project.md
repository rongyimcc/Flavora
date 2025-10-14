# Flavora 项目说明文档

<div align="center">

![Flavora Logo](logo.png)

**Flavora - 美食分享社交平台**

一个基于 Android + Firebase 的现代化美食分享应用

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com/)
[![Material Design](https://img.shields.io/badge/Design-Material%203-blue.svg)](https://m3.material.io/)
[![Java 11](https://img.shields.io/badge/Java-11-red.svg)](https://www.oracle.com/java/)

</div>

---

## 📖 项目简介

Flavora 是一个专注于美食分享的社交应用，用户可以发布美食帖子、浏览他人分享、点赞收藏喜欢的内容。应用采用现代化的 Material Design 3 设计语言，支持深色模式，提供流畅的用户体验。

### 核心特性

✨ **美食分享** - 支持多图上传，评分系统，详细描述
🔍 **智能搜索** - 实时搜索标题、描述、用户名
👍 **互动功能** - 点赞、收藏、评论（规划中）
👤 **个人中心** - 查看我的帖子、收藏，管理个人信息
🌓 **深色模式** - 完美适配深色主题
🔐 **用户认证** - 基于 Firebase Authentication 的安全登录

---

## 🏗️ 项目架构

Flavora 采用经典的 **分层架构** 设计，确保代码的可维护性和可扩展性。

```
┌─────────────────────────────────────────────┐
│              UI Layer (用户界面层)              │
│  Activities, Fragments, Adapters, Dialogs   │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│           Facade Layer (业务门面层)            │
│        PostFacade, PostInteractionFacade     │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│         Repository Layer (仓库层)             │
│      AuthRepository, StorageRepository       │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│            DAO Layer (数据访问层)              │
│   UserDAO, PostDAO, LikeDAO, FavoriteDAO    │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│        DataSource Layer (数据源层)            │
│          FirebaseDataSource (单例)           │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│           Model Layer (数据模型层)             │
│       User, Post, Like, Favorite (POJO)     │
└─────────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│              Firebase Services               │
│  Authentication, Firestore, Storage          │
└─────────────────────────────────────────────┘
```

### 架构特点

1. **单向依赖** - 上层依赖下层，下层不依赖上层
2. **单一职责** - 每层专注于特定功能
3. **松耦合** - 层与层之间通过接口通信
4. **易测试** - 每层可独立测试

---

## 📦 模块说明

### 1. Model Layer（数据模型层）

定义应用的核心数据结构，对应 Firestore 数据库的文档结构。

#### User（用户）
```java
public class User implements HasUUID {
    private String userId;        // 用户ID
    private String username;      // 用户名
    private String email;         // 邮箱
    private String avatarUrl;     // 头像URL
    private int postsCount;       // 帖子数量
    private Timestamp createdAt;  // 创建时间
}
```

#### Post（帖子）
```java
public class Post implements HasUUID {
    private String postId;           // 帖子ID
    private String userId;           // 作者ID
    private String username;         // 作者用户名
    private String userAvatarUrl;    // 作者头像
    private String title;            // 标题
    private String description;      // 描述
    private List<String> imageUrls;  // 图片URL列表
    private double rating;           // 评分（0-5星）
    private int likeCount;           // 点赞数
    private int favoriteCount;       // 收藏数
    private Timestamp createdAt;     // 创建时间
}
```

#### Like（点赞）
```java
public class Like {
    private String userId;       // 点赞用户ID
    private String postId;       // 帖子ID
    private Timestamp likedAt;   // 点赞时间
}
```

#### Favorite（收藏）
```java
public class Favorite {
    private String userId;          // 收藏用户ID
    private String postId;          // 帖子ID
    private Timestamp favoritedAt;  // 收藏时间
}
```

---

### 2. DAO Layer（数据访问对象层）

提供对 DataSource 的抽象访问，实现 CRUD 操作。

- **UserDAO** - 用户数据访问
- **PostDAO** - 帖子数据访问
- **LikeDAO** - 点赞数据访问
- **FavoriteDAO** - 收藏数据访问

每个 DAO 都实现了 `DAO<T>` 接口，提供统一的数据操作方法。

**设计特点：**
- 单例模式（线程安全）
- 统一接口（add, get, getAll, update, delete）
- 异步回调（OnCompleteListener）

---

### 3. DataSource Layer（数据源层）

**FirebaseDataSource** - 封装所有 Firebase 操作的单例类。

#### 主要功能分区：

**用户操作（User Operations）**
- 添加/获取/更新/删除用户
- 根据用户名查询
- 用户帖子计数管理

**帖子操作（Post Operations）**
- 添加/获取/更新/删除帖子
- 按时间排序查询
- 按用户查询帖子
- 帖子点赞/收藏计数管理

**点赞操作（Like Operations）**
- 添加/删除点赞
- 原子操作：点赞+计数增加
- 原子操作：取消点赞+计数减少
- 查询用户点赞列表
- 检查点赞状态

**收藏操作（Favorite Operations）**
- 添加/删除收藏
- 原子操作：收藏+计数增加
- 原子操作：取消收藏+计数减少
- 查询用户收藏列表
- 获取完整收藏帖子对象

**设计特点：**
- 使用 WriteBatch 实现原子操作，确保数据一致性
- 双重检查锁定（DCL）保证线程安全
- 统一使用 OnCompleteListener 回调

---

### 4. Repository Layer（仓库层）

提供特定功能领域的数据访问接口。

#### AuthRepository（认证仓库）
- 用户注册/登录/登出
- 获取当前用户信息
- 用户名唯一性检查
- 自动用户资料创建

#### StorageRepository（存储仓库）
- 图片上传到 Firebase Storage
- 生成下载 URL
- 文件路径管理（`images/{userId}/{timestamp}.jpg`）

---

### 5. Facade Layer（业务门面层）

简化复杂业务逻辑，为 UI 层提供简洁的接口。

#### PostFacade（帖子门面）
- `createPost()` - 创建帖子（上传图片 + 保存数据 + 更新用户计数）
- `getAllPosts()` - 获取所有帖子（按时间排序）
- `getPostsByUser()` - 获取用户帖子
- `getFavoritedPostsByUser()` - 获取收藏帖子
- `deletePost()` - 删除帖子（删除数据 + 更新用户计数）

#### PostInteractionFacade（互动门面）
- `toggleLike()` - 切换点赞状态（智能判断添加/删除）
- `toggleFavorite()` - 切换收藏状态
- `getLikedPostIds()` - 获取当前用户点赞列表
- `getFavoritedPostIds()` - 获取当前用户收藏列表

---

### 6. UI Layer（用户界面层）

采用 Fragment + Activity 架构，使用 ViewBinding 进行视图绑定。

#### 主框架
- **MainActivity** - 底部导航主活动
  - Navigation Component 导航管理
  - 三个 Tab：发现、Dashboard、个人中心

#### 认证模块
- **LoginActivity** - 登录页面
  - 邮箱/密码登录
  - 表单验证
  - 错误提示
- **RegisterActivity** - 注册页面
  - 用户名/邮箱/密码注册
  - 用户名唯一性检查
  - 自动创建用户资料

#### 发现模块
- **DiscoverFragment** - 发现页面
  - 帖子列表（RecyclerView）
  - 实时搜索（TextWatcher）
  - 下拉刷新（SwipeRefreshLayout）
  - 点赞/收藏互动
- **PostsAdapter** - 帖子列表适配器
  - ViewHolder 模式
  - 图片轮播（ViewPager2）
  - 点赞/收藏按钮状态管理
  - 删除按钮（条件显示）
- **PostImageAdapter** - 图片轮播适配器
  - Glide 图片加载
  - 点击查看大图

#### 帖子模块
- **CreatePostBottomSheet** - 创建帖子底部表单
  - 多图选择（最多 5 张）
  - 图片预览和移除
  - 标题/描述输入
  - 评分条（RatingBar）
  - 上传进度显示
- **PostDetailActivity** - 帖子详情页
  - 完整帖子信息展示
  - 图片轮播
  - 点赞/收藏互动
  - 作者信息展示
- **SelectedImagesAdapter** - 已选图片适配器
  - 图片预览
  - 移除按钮

#### 个人中心模块
- **ProfileFragment** - 个人中心页面
  - 用户头像和基本信息
  - ViewPager2 + TabLayout
  - 两个子页面：我的帖子、我的收藏
- **MyPostsFragment** - 我的帖子列表
  - 展示用户发布的所有帖子
  - 支持删除（带确认对话框）
  - 点赞/收藏互动
- **MyFavoritesFragment** - 我的收藏列表
  - 展示用户收藏的所有帖子
  - 取消收藏自动刷新列表
- **SettingsBottomSheet** - 设置底部表单
  - 深色模式切换
  - 登出功能
- **ProfilePagerAdapter** - ViewPager 适配器
  - Fragment 页面管理

---

## 🔥 Firebase 集成

### Firebase Services 使用说明

#### 1. Firebase Authentication（认证服务）

**用途：** 用户注册、登录、身份验证

**配置文件：** `google-services.json`

**使用方式：**
```java
FirebaseAuth auth = FirebaseAuth.getInstance();
auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            FirebaseUser user = auth.getCurrentUser();
        }
    });
```

**启用方法：**
- Firebase Console → Authentication → Sign-in method
- 启用 Email/Password 提供商

---

#### 2. Cloud Firestore（云数据库）

**用途：** 存储用户、帖子、点赞、收藏数据

**数据结构：**
```
firestore/
├── users/          # 用户集合
│   └── {userId}    # 用户文档
├── posts/          # 帖子集合
│   └── {postId}    # 帖子文档
├── likes/          # 点赞集合
│   └── {likeId}    # 点赞文档 (userId_postId)
└── favorites/      # 收藏集合
    └── {favoriteId}  # 收藏文档 (userId_postId)
```

**索引配置：**
- `posts` 集合：`createdAt DESC`
- `posts` 集合：`userId ASC, createdAt DESC`
- `likes` 集合：`userId ASC`
- `favorites` 集合：`userId ASC`

**安全规则：**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // 用户只能读取所有数据，但只能修改自己的数据
    match /users/{userId} {
      allow read: if true;
      allow write: if request.auth.uid == userId;
    }

    match /posts/{postId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth.uid == resource.data.userId;
    }

    match /likes/{likeId} {
      allow read: if true;
      allow create, delete: if request.auth != null;
    }

    match /favorites/{favoriteId} {
      allow read: if true;
      allow create, delete: if request.auth != null;
    }
  }
}
```

---

#### 3. Firebase Storage（云存储）

**用途：** 存储用户上传的美食图片

**存储路径：**
```
storage/
└── images/
    └── {userId}/
        ├── {timestamp1}.jpg
        ├── {timestamp2}.jpg
        └── ...
```

**使用方式：**
```java
StorageReference ref = storage.getReference()
    .child("images/" + userId + "/" + timestamp + ".jpg");
ref.putFile(imageUri)
    .addOnSuccessListener(taskSnapshot -> {
        ref.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
        });
    });
```

**安全规则：**
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /images/{userId}/{filename} {
      // 只有认证用户可以上传到自己的目录
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

**图片要求：**
- 格式：JPEG, PNG
- 大小限制：5MB
- 压缩：客户端压缩至 1920x1080

---

### Firebase 配置步骤

1. **创建 Firebase 项目**
   - 访问 [Firebase Console](https://console.firebase.google.com/)
   - 点击"添加项目"
   - 输入项目名称：Flavora

2. **添加 Android 应用**
   - 点击 Android 图标
   - 输入包名：`comp.assignment.flavora`
   - 下载 `google-services.json`
   - 放置到 `app/` 目录

3. **添加 Firebase SDK**
   - 项目已配置好依赖（见 `app/build.gradle.kts`）
   - 无需额外配置

4. **启用服务**
   - Authentication：启用 Email/Password
   - Firestore：创建数据库（生产模式）
   - Storage：启用存储服务

5. **配置安全规则**
   - 复制上述安全规则到 Firebase Console
   - 发布规则

---

## 🎨 UI 设计

### Material Design 3

Flavora 严格遵循 Material Design 3 设计规范，提供现代化的用户界面。

#### 颜色系统

**品牌色（黄色系列）：**
```xml
<color name="yellow_500">#FFC107</color>  <!-- 主要品牌色 -->
<color name="yellow_700">#FFA000</color>  <!-- 深黄色 -->
<color name="yellow_200">#FFE082</color>  <!-- 浅黄色 -->
```

**中性色：**
```xml
<color name="black">#FF000000</color>
<color name="white">#FFFFFFFF</color>
<color name="gray_100">#FFF5F5F5</color>
<color name="gray_300">#FFE0E0E0</color>
<color name="gray_500">#FF9E9E9E</color>
<color name="gray_700">#FF616161</color>
```

**深色模式：**
- 背景色：`#121212`
- 卡片色：`#1E1E1E`
- 文字色：`#FFFFFF`（87% 透明度）

#### Material Icons

Flavora 使用 **Google Material Icons** 作为统一的图标库。

**图标分类：**

| 类别 | 图标 | 文件名 | 用途 |
|------|------|--------|------|
| 导航 | 🏠 | `ic_home_black_24dp.xml` | 首页 |
| 导航 | 📊 | `ic_dashboard_black_24dp.xml` | 仪表盘 |
| 导航 | 🔔 | `ic_notifications_black_24dp.xml` | 通知 |
| 发现 | 🔍 | `ic_search_24.xml` | 搜索 |
| 发现 | 🧭 | `ic_explore_24.xml` | 探索 |
| 互动 | 👍 | `ic_thumb_up_24.xml` | 实心点赞 |
| 互动 | 👍 | `ic_thumb_up_border_24.xml` | 空心点赞 |
| 互动 | 🔖 | `ic_bookmark_24.xml` | 实心收藏 |
| 互动 | 🔖 | `ic_bookmark_border_24.xml` | 空心收藏 |
| 互动 | ❤️ | `ic_favorite_24.xml` | 实心喜欢 |
| 互动 | ❤️ | `ic_favorite_border_24.xml` | 空心喜欢 |
| 评分 | ⭐ | `ic_star_rate_24.xml` | 星级评分 |
| 操作 | ➕ | `ic_add_24.xml` | 添加 |
| 操作 | ✏️ | `ic_note_add_24.xml` | 添加笔记 |
| 操作 | 🖼️ | `ic_image_24.xml` | 图片 |
| 操作 | ✖️ | `ic_close_24.xml` | 关闭 |
| 操作 | ✖️ | `ic_close_white_24.xml` | 白色关闭 |
| 操作 | 🔙 | `ic_arrow_back_24.xml` | 返回 |
| 操作 | 🗑️ | `delete_sweep_24px.xml` | 删除清除 |
| 用户 | 👤 | `ic_person_24.xml` | 用户 |
| 设置 | 🎛️ | `ic_tune_24.xml` | 调整设置 |

**图标特点：**
- 24dp 尺寸（标准触摸目标：48dp）
- 矢量图（Vector Drawable），支持任意缩放
- 自动适配深色模式
- 语义化命名

**图标使用示例：**
```xml
<ImageView
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_thumb_up_24"
    android:tint="?attr/colorOnSurface" />
```

---

### 布局设计

#### 1. 帖子卡片（item_post.xml）
- 用户头像和用户名（顶部）
- 图片轮播（ViewPager2）
- 图片指示器（1/5）
- 标题和描述
- 评分条（RatingBar）
- 点赞/收藏按钮（底部）
- 发布时间（相对时间）

#### 2. 底部表单（BottomSheet）
- 创建帖子表单
- 设置表单
- 圆角设计
- 半透明背景遮罩

#### 3. 搜索栏
- Material Design 搜索框
- 实时搜索反馈
- 清空按钮

#### 4. 空状态
- 友好的空状态提示
- 引导用户操作

---

## 🛠️ 技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **编程语言** | Java | 11 | 核心开发语言 |
| **最低 SDK** | Android | 34 | Android 14 |
| **目标 SDK** | Android | 36 | Android 最新版 |
| **构建工具** | Gradle | 8.x | Kotlin DSL 配置 |
| **后端服务** | Firebase | - | 全套云服务 |
| **数据库** | Firestore | - | NoSQL 云数据库 |
| **认证** | Firebase Auth | - | 用户认证 |
| **存储** | Firebase Storage | - | 图片存储 |
| **UI框架** | Material Design | 3 | 现代化 UI 组件 |
| **图片加载** | Glide | 4.16.0 | 高性能图片加载 |
| **导航** | Navigation Component | 2.8.5 | Fragment 导航 |
| **视图绑定** | ViewBinding | - | 类型安全视图访问 |
| **图标** | Material Icons | - | Google 官方图标 |

---

## 📱 应用截图

### 浅色模式
（此处可插入截图）

### 深色模式
（此处可插入截图）

---

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高
- JDK 11 或更高
- Android SDK 34 或更高
- 已安装 Firebase CLI（可选）

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-repo/Flavora.git
cd Flavora
```

2. **配置 Firebase**
- 下载 `google-services.json` 放到 `app/` 目录
- 配置 Firebase Authentication、Firestore、Storage

3. **打开项目**
- 使用 Android Studio 打开项目
- 等待 Gradle 同步完成

4. **运行应用**
```bash
./gradlew installDebug
```

---

## 📝 代码规范

### 命名规范
- **类名：** PascalCase（如 `PostAdapter`）
- **方法名：** camelCase（如 `loadPosts()`）
- **常量：** UPPER_SNAKE_CASE（如 `COLLECTION_POSTS`）
- **资源ID：** snake_case（如 `button_submit`）

### 注释规范
所有公共类和方法必须有中文 JavaDoc 注释：
```java
/**
 * 获取所有帖子列表
 * <p>
 * 从Firestore按创建时间降序查询所有帖子。
 * </p>
 *
 * @param listener 完成监听器，返回帖子列表或异常
 */
public void getAllPosts(OnCompleteListener<List<Post>> listener) {
    // 实现代码
}
```

### 架构规范
- UI 层调用 Facade 层
- Facade 层调用 Repository 或 DAO 层
- DAO 层调用 DataSource 层
- 不允许跨层调用

---

## 🔧 构建配置

### Gradle 配置

**项目级 build.gradle：**
```kotlin
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

**应用级 build.gradle.kts：**
```kotlin
android {
    compileSdk = 36
    defaultConfig {
        minSdk = 34
        targetSdk = 36
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // UI
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.navigation:navigation-fragment:2.8.5")

    // 图片加载
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

---

## 🧪 测试

### 单元测试
```bash
./gradlew test
```

### UI 测试
```bash
./gradlew connectedAndroidTest
```

### Lint 检查
```bash
./gradlew lint
```

---

## 📊 项目统计

- **代码文件：** 31 个 Java 文件
- **布局文件：** 12 个 XML 文件
- **代码行数：** 约 6500 行
- **图标数量：** 30+ 个 Material Icons
- **支持语言：** 中文
- **支持主题：** 浅色 + 深色

---

## 👥 团队成员

- **组员 1** - 用户认证与基础架构
- **组员 2** - 帖子核心模块
- **组员 3** - 发现页面与展示
- **组员 4** - 互动功能模块
- **组员 5** - 个人中心与主框架

---

## 📄 许可证

本项目仅用于学习和课程作业，未开源。

---

## 🙏 致谢

- **Firebase** - 提供强大的云服务
- **Google Material Design** - 优秀的设计规范和图标库
- **Glide** - 高效的图片加载库
- **Android Jetpack** - 现代化的 Android 开发组件

---

## 📮 联系方式

如有问题或建议，请联系项目团队。

---

<div align="center">

**Flavora** - 分享美食，分享生活

Made with ❤️ by Flavora Team

</div>
