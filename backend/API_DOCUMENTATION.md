# BlueBook 后端接口文档

> **基础地址：** `http://localhost:8081`
> 
> **统一响应格式：**
> ```json
> {
>   "code": 1,      // 1=成功, 0=失败, 401=未登录
>   "msg": "...",   // 错误时携带提示信息
>   "data": {}      // 业务数据
> }
> ```

---

<p align="center">
  <h1 align="center">📕 BlueBook API</h1>
  <p align="center">
    仿小红书社区平台 · 前后端分离架构 · 全量接口文档
  </p>
  <p align="center">
    <img src="https://img.shields.io/badge/模块-用户认证与授权-3a64ff?style=for-the-badge" />
    <img src="https://img.shields.io/badge/模块-创作中心-ff2442?style=for-the-badge" />
    <img src="https://img.shields.io/badge/模块-社交互动-00c853?style=for-the-badge" />
    <img src="https://img.shields.io/badge/模块-内容推荐与发现-ff6d00?style=for-the-badge" />
    <img src="https://img.shields.io/badge/模块-后台管理-aa00ff?style=for-the-badge" />
  </p>
</p>

---

## 📖 五大核心业务模块总览

| # | 模块 | 说明 | 覆盖路径 |
|:-:|------|------|----------|
| 1 | <img src="https://img.shields.io/badge/1-用户认证与授权-3a64ff" /> | 注册登录、JWT Token 管理、OAuth 第三方登录、权限控制 | `/api/auth/*`、`/admin/login` |
| 2 | <img src="https://img.shields.io/badge/2-创作中心-ff2442" /> | 文章发布/编辑/删除、草稿管理、图片上传、分类与标签管理 | `/api/create-center/*` |
| 3 | <img src="https://img.shields.io/badge/3-社交互动-00c853" /> | 评论互动、关注关系、点赞行为、私信聊天、社区首页 | 多路径覆盖 |
| 4 | <img src="https://img.shields.io/badge/4-内容推荐与发现-ff6d00" /> | 个性化推荐流、关注动态流、全文搜索、文章详情 | 多路径覆盖 |
| 5 | <img src="https://img.shields.io/badge/5-后台管理-aa00ff" /> | 内容审核、敏感词管理、文章管理、用户管理 | `/sys/*`、`/admin/*` |

---

<br>

# <img src="https://img.shields.io/badge/模块一-用户认证与授权-3a64ff" />

> 涵盖用户注册、登录、JWT 双 Token 刷新、QQ 邮箱登录、GitHub OAuth 三方登录等身份认证全流程。

---

### 1.1 用户密码登录

> 使用用户名和密码进行登录，登录成功后返回 JWT 双 Token（accessToken + refreshToken）。

```
POST /api/auth/login
```

**请求体参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**成功响应示例：**
```json
{
  "code": 1,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": "1800000"
  }
}
```

**失败响应：** `{ "code": 0, "msg": "用户名或密码错误" }`

---

### 1.2 刷新 Token

> 当 accessToken 过期时，使用 refreshToken 获取新的 Token 对。支持从 Cookie 或请求体中获取 refreshToken。

```
POST /api/auth/refresh
```

**请求体（可选）：**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### 1.3 用户注册

> 新用户注册，需要携带邮箱验证码进行身份校验。

```
POST /api/auth/register
```

**请求体参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 是 | QQ 邮箱 |
| code | String | 是 | 邮箱验证码 |

---

### 1.4 发送 QQ 邮箱验证码

> 向指定 QQ 邮箱发送登录/注册验证码。

```
POST /api/auth/QQ/sendCode
```

**请求体：**
```json
{
  "email": "3550478xxx@qq.com"
}
```

---

### 1.5 QQ 邮箱登录

> 使用 QQ 邮箱 + 验证码进行快捷登录，无需密码。

```
POST /api/auth/QQ/login
```

**请求体参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | QQ 邮箱 |
| code | String | 是 | 收到的验证码 |

---

### 1.6 验证码校验

> 校验用户输入的验证码是否正确，常用于注册流程中的验证步骤。

```
POST /api/auth/verify-code
```

**请求体：**
```json
{
  "email": "3550478xxx@qq.com",
  "code": "123456"
}
```

---

### 1.7 退出登录

> 清除服务端登录态和 Cookie 中的 Token。

```
POST /api/auth/loginOut
```

---

### 1.8 GitHub OAuth 登录

> 获取 GitHub 第三方登录的重定向地址，前端引导用户跳转 GitHub 授权页。

```
POST /api/oauth/github/login
```

**成功响应：**
```json
{
  "code": 1,
  "data": {
    "message": "请使用GitHub登录",
    "github_login_url": "/oauth2/authorization/github"
  }
}
```

---

### 1.9 管理后台登录

> 管理员使用账号密码登录后台管理系统。

```
POST /admin/login
```

**请求体：**
```json
{
  "username": "admin",
  "password": "123456"
}
```

---

### 1.10 管理后台注册

> 管理员注册新账号（需具备管理员权限）。

```
POST /admin/register
```

---

<br>

# <img src="https://img.shields.io/badge/模块二-创作中心-ff2442" />

> 用户内容创作的核心模块，涵盖文章完整生命周期：写草稿 → 发布 → 审核 → 编辑 → 删除，以及图片上传、标签与分类管理。

---

### 2.1 发布文章

> 发布一篇新文章，包含标题、正文、封面、分类、标签等信息。发布后自动进入内容审核流程。

```
POST /api/create-center
```

**请求体参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 文章标题（限 20 字） |
| content | String | 是 | 文章正文 |
| summary | String | 否 | 文章摘要 |
| categoryId | Long | 是 | 分类 ID |
| label | Long[] | 否 | 已有标签 ID 列表 |
| customLabels | String[] | 否 | 自定义标签名称列表 |
| coverImageUrl | String | 否 | 封面图片 URL |
| isCommented | Long | 是 | 是否允许评论（1=允许） |

**请求体示例：**
```json
{
  "title": "我的第一篇笔记",
  "content": "这是正文内容...",
  "summary": "这是摘要",
  "categoryId": 1,
  "label": [1, 2],
  "customLabels": ["旅行", "美食"],
  "coverImageUrl": "https://minio.example.com/images/xxx.jpg",
  "isCommented": 1
}
```

---

### 2.2 保存草稿

> 将文章保存为草稿，暂不提交审核。支持后续继续编辑完善。

```
POST /api/create-center/draft
```

**请求体：** 同 `2.1 发布文章`

---

### 2.3 上传图片

> 上传文章配图到 MinIO 对象存储，支持单张上传。

```
POST /api/create-center/upload/image
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | 图片文件 |

---

### 2.4 删除图片

> 从 MinIO 存储中删除已上传的图片。

```
POST /api/create-center/upload/image/delete?imageUrl=string
```

---

### 2.5 查看草稿箱

> 获取当前用户的所有草稿文章列表。

```
GET /api/create-center/drafts
```

---

### 2.6 查看已发布文章

> 获取当前用户所有已发布（审核通过）的文章列表。

```
GET /api/create-center/published
```

---

### 2.7 查看待审核文章

> 获取当前用户所有待审核（提交后尚未处理）的文章列表。

```
GET /api/create-center/pending
```

---

### 2.8 查看未通过文章

> 获取当前用户所有审核未通过（被拦截或拒绝）的文章列表。

```
GET /api/create-center/rejected
```

---

### 2.9 查看全部文章

> 获取当前用户所有状态（草稿/待审核/已发布/未通过）的文章。

```
GET /api/create-center/all
```

---

### 2.10 编辑回显

> 编辑文章时回显已有数据，返回文章详情及各字段当前值。标签以 `{id, name}` 格式返回，前端可区分已有标签与自定义标签。

```
GET /api/create-center/edit/{articleId}
```

**成功响应：**
```json
{
  "code": 1,
  "data": {
    "id": 1,
    "title": "我的第一篇笔记",
    "content": "正文内容...",
    "summary": "摘要",
    "coverImageUrl": "https://minio.example.com/images/xxx.jpg",
    "categoryId": 1,
    "status": 1,
    "label": [
      { "id": 1, "name": "旅行" },
      { "id": 2, "name": "美食" }
    ]
  }
}
```

---

### 2.11 编辑草稿

> 对已存在的草稿进行编辑更新。

```
PUT /api/create-center/drafts/{draftId}
```

---

### 2.12 删除草稿

> 删除指定的草稿文章。

```
DELETE /api/create-center/drafts/{draftId}
```

---

### 2.13 删除已发布文章

> 删除已发布的文章（软删除或物理删除取决于实现）。

```
DELETE /api/create-center/{articleId}
```

---

### 2.14 查看单篇文章详情

> 在创作中心中查看指定文章的完整详情，包含作者信息和标签。

```
GET /api/create-center/detail/{articleId}
```

---

### 2.15 按标题筛选文章

> 在创作中心中根据标题关键字筛选文章，支持按状态过滤。

```
GET /api/create-center/by-title?title=关键字&status=1
```

| 参数 | 类型 | 说明 |
|------|------|------|
| title | String | 标题关键字（模糊匹配） |
| status | Integer | 文章状态（1=草稿, 2=待审核, 3=已发布） |

---

### 2.16 按日期筛选文章

> 在创作中心中根据创建日期筛选文章。

```
GET /api/create-center/by-date?date=2026-01-01&status=1
```

| 参数 | 类型 | 说明 |
|------|------|------|
| date | String | 日期（yyyy-MM-dd） |
| status | Integer | 文章状态 |

---

### 2.17 获取全部分类

> 获取系统中所有可用文章分类，用于发布页面的分类选择器。

```
GET /api/create-center/categories
```

**成功响应：**
```json
{
  "code": 1,
  "data": [
    { "id": 1, "title": "旅行" },
    { "id": 2, "title": "美食" },
    { "id": 3, "title": "穿搭" }
  ]
}
```

---

<br>

# <img src="https://img.shields.io/badge/模块三-社交互动-00c853" />

> 用户间的社交行为体系，包含评论互动、关注关系、点赞行为、私信聊天以及社区首页展示。

---

## 📝 评论系统

### 3.1 获取用户基本信息

> 获取当前登录用户的个人信息，用于发表评论时的身份展示。

```
GET /api/comment/userProfile
```

---

### 3.2 发表评论

> 对文章发表评论，支持回复评论（二级评论）。

```
POST /api/comment/publish
```

**请求体参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | Long | 是 | 文章 ID |
| content | String | 是 | 评论内容 |
| parentId | Long | 否 | 父评论 ID（回复时必填） |
| rootId | Long | 否 | 根评论 ID（回复时必填） |

**请求体示例：**
```json
{
  "articleId": 1,
  "content": "写得太好了！",
  "parentId": 0,
  "rootId": 0
}
```

---

### 3.3 获取文章评论列表

> 分页获取指定文章的所有评论。

```
GET /api/comment/{articleId}
```

---

## 👤 关注系统

### 3.4 关注/取消关注用户

> 关注或取消关注某个用户。已关注时调用为取消关注，未关注时调用为关注。

```
POST /api/Interaction/follow/{userId}
```

| 参数 | 位置 | 说明 |
|------|------|------|
| userId | Path | 要关注的目标用户 ID |

---

### 3.5 检查是否已关注

> 检查当前登录用户是否已关注指定用户。

```
GET /api/Interaction/follow/is-following/{userId}
```

---

### 3.6 获取粉丝数

> 获取指定用户的粉丝数量。

```
GET /api/Interaction/follow/followers/count/{userId}
```

---

### 3.7 获取关注数

> 获取指定用户的关注数量。

```
GET /api/Interaction/follow/following/count/{userId}
```

---

## ❤️ 点赞系统

### 3.8 点赞/取消点赞

> 对文章或评论进行点赞操作。已点赞时调用为取消点赞，未点赞时调用为点赞。

```
POST /user/api/Interaction/likes
```

**请求体参数：**

| 字段 | 类型 | 说明 |
|------|------|------|
| entityId | Long | 实体 ID（文章 ID 或评论 ID） |
| loveTypeId | Long | 点赞类型（1=文章, 2=评论） |

**请求体示例：**
```json
{
  "entityId": 1,
  "loveTypeId": 1
}
```

---

### 3.9 检查是否已点赞

> 检查当前用户是否对某实体（文章/评论）点过赞。

```
GET /user/api/Interaction/is-liked?entityId=1&loveTypeId=1
```

---

### 3.10 获取点赞数

> 获取指定实体的总点赞数。

```
GET /user/api/Interaction/count?entityId=1&loveTypeId=1
```

---

## 💬 私信系统

### 3.11 获取好友列表

> 获取当前用户的关注用户列表，用于私信联系人选择。

```
GET /api/private/{userId}
```

---

### 3.12 获取聊天记录

> 获取当前用户与某个好友的历史聊天记录。

```
GET /api/private/ChatHistory/{friendId}
```

---

## 🏠 社区首页

### 3.13 获取用户头像

> 获取当前登录用户的头像 URL 和用户 ID，用于社区首页顶部展示。

```
GET /api/community/user/avatar
```

---

### 3.14 判断是否登录

> 检查当前请求是否携带有效的用户登录态。

```
GET /api/community/isLogin
```

---

### 3.15 获取未读消息数

> 获取当前用户的私信未读消息数量，用于消息红点提示。

```
GET /api/community/unreadMessage?userId=1
```

---

<br>

# <img src="https://img.shields.io/badge/模块四-内容推荐与发现-ff6d00" />

> 内容分发的核心引擎，包含个性化推荐流、关注动态流、全文搜索、文章详情页等功能。

---

### 4.1 首页推荐流

> 登录用户获取个性化推荐笔记列表（协同过滤 + 热门 + 标签多路召回），未登录用户获取热门笔记列表。支持游标分页。

```
GET /api/Recommend/feed?userId=1&offset=0&limit=20
```

**请求参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| userId | Long | - | 用户 ID（登录后必传，未登录不传） |
| offset | int | 0 | 分页偏移量（从第几条开始） |
| limit | int | 20 | 每页返回数量 |

**成功响应：**
```json
{
  "code": 1,
  "data": {
    "hasMore": true,
    "notes": [
      {
        "articleId": 1,
        "title": "笔记标题",
        "coverImageUrl": "https://minio.example.com/cover.jpg",
        "loveCount": 128,
        "author": "作者名"
      }
    ]
  }
}
```

---

### 4.2 关注动态流

> 获取当前用户关注作者的最新文章动态。优先从 Redis 时间线（ZSet）读取近 7 天文章；如果时间线为空（关注作者 7 天内未发布），则降级查询每个关注作者的最新一篇已发布文章。

```
GET /api/dynamic/newArticles
```

**业务逻辑：**
1. 从 Redis ZSet 中读取当前用户时间线中 7 天内的文章
2. 若 ZSet 无数据 → 查询所有关注作者的最新文章（每人取最新 1 篇）
3. 返回文章列表（含作者头像、标题、封面等信息）

---

### 4.3 社区推荐文章

> 获取社区首页的固定推荐文章列表。

```
GET /api/community/Recommend
```

---

### 4.4 文章详情

> 获取文章完整详情，包含作者信息、内容、点赞量、浏览量等。适用于搜索页面、首页等外部入口点击进入。

```
GET /api/articles/{articleId}
```

---

### 4.5 搜索文章

> 根据关键字搜索已发布的文章，支持标题模糊匹配。

```
GET /api/search/articles?keyword=搜索关键词
```

---

<br>

# <img src="https://img.shields.io/badge/模块五-后台管理-aa00ff" />

> 运营管理后台，包含内容审核流程、敏感词管理、文章状态管理和用户管理。

---

## 📋 内容审核

### 5.1 获取待审核列表

> 分页获取所有待人工审核的内容列表（支持文章类型），供运营人员处理。

```
GET /sys/review/pending?page=1&size=10&type=article
```

**请求参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 1 | 页码 |
| size | int | 10 | 每页数量 |
| type | string | article | 审核内容类型 |

---

### 5.2 内容审核 - 通过

> 审核通过指定待审核内容，将其标记为正常发布状态。

```
POST /sys/review/approve?recordId=1&remark=备注
```

| 参数 | 类型 | 说明 |
|------|------|------|
| recordId | Long | 审核记录 ID |
| remark | String | 审核备注（可选） |

---

### 5.3 内容审核 - 拒绝

> 审核拒绝指定内容，将其标记为拦截状态。用户端会看到审核未通过的提示。

```
POST /sys/review/reject?recordId=1&remark=备注
```

| 参数 | 类型 | 说明 |
|------|------|------|
| recordId | Long | 审核记录 ID |
| remark | String | 拒绝原因（可选） |

---

## 🔞 敏感词管理

### 5.4 敏感词列表

> 获取系统中所有已配置的敏感词列表。

```
GET /sys/sensitive-word/list
```

---

### 5.5 添加敏感词

> 新增一个敏感词到词库中，添加后自动触发词库重载，立即生效。

```
POST /sys/sensitive-word/add?word=赌博&level=1
```

| 参数 | 类型 | 说明 |
|------|------|------|
| word | String | 敏感词内容 |
| level | Integer | 严重等级（数值越大越严重，默认 1） |

---

### 5.6 删除敏感词

> 从词库中删除指定敏感词，删除后自动重载词库。

```
POST /sys/sensitive-word/delete?id=1
```

---

### 5.7 重载词库

> 手动触发敏感词库热重载，适用于批量修改词库后强制刷新。

```
POST /sys/sensitive-word/reload
```

---

## 📰 文章管理

### 5.8 查看待审核和已发布文章

> 管理员查看所有待审核和已发布状态的文章列表。

```
GET /api/admin/articles/review-and-publish
```

---

### 5.9 更新文章状态

> 管理员直接修改文章的状态（审核、发布、拦截等）。

```
PUT /api/admin/articles/{articleId}?status=1
```

**文章状态说明：**

| status | 含义 | 说明 |
|--------|------|------|
| 0 | 待审核 | 用户已提交，等待审核 |
| 1 | 正常（已发布） | 审核通过，前台可见 |
| 2 | 拦截 | 审核未通过，已被屏蔽 |
| 3 | 人工复核 | AI 审核不确定，待人工判断 |

---

## 👥 用户管理

### 5.10 用户列表

> 分页获取所有注册用户的信息列表。

```
GET /admin/user?pageNum=1&pageSize=10
```

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |

---

### 5.11 添加用户

> 管理员手动添加新用户。

```
POST /admin/user/add
```

**请求体：**
```json
{
  "username": "newuser",
  "password": "123456",
  "email": "user@qq.com"
}
```

---

### 5.12 获取单个用户

> 根据用户 ID 获取用户详情。

```
GET /admin/user/{id}
```

---

<br>

---

<p align="center">
  <i>— 文档结束 —</i>
  <br>
  <sub>BlueBook Backend API · 共 38 个接口 · 5 大核心业务模块</sub>
</p>