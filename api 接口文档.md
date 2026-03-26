# 博客系统 API 接口文档

本文档描述了博客系统的所有 RESTful API 接口，包含用户模块、分类模块和文章模块的完整接口说明。

---

## 目录

- [一、用户模块 (UserController)](#一用户模块-usercontroller)
  - [1.1 用户登录](#11-用户登录)
  - [1.2 用户注册](#12-用户注册)
  - [1.3 修改密码](#13-修改密码)
  - [1.4 退出登录](#14-退出登录)
- [二、分类模块 (CategoryController)](#二分类模块-categorycontroller)
  - [2.1 创建主标签](#21-创建主标签)
  - [2.2 创建子标签](#22-创建子标签)
  - [2.3 删除标签](#23-删除标签)
  - [2.4 获取标签树](#24-获取标签树)
- [三、文章模块 (ArticleController)](#三文章模块-articlecontroller)
  - [3.1 创建文章](#31-创建文章)
  - [3.2 获取文章列表](#32-获取文章列表)
  - [3.3 获取我的文章列表](#33-获取我的文章列表)
  - [3.4 删除文章](#34-删除文章)
  - [3.5 更新文章置顶状态](#35-更新文章置顶状态)
  - [3.6 获取文章详情](#36-获取文章详情)
- [四、数据结构说明](#四数据结构说明)
- [五、错误码说明](#五错误码说明)

---

# 一、用户模块 (UserController)

**基础路径**: `/api/user`

## 1.1 用户登录

### 基本信息
- **接口名称**: 用户登录
- **请求方法**: POST
- **请求路径**: `/api/user/login`
- **功能描述**: 验证用户凭据，登录成功后返回用户信息和 JWT Token

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| username | String | 是 | `"admin"` | 用户名，3-6 位字母数字下划线 |
| password | String | 是 | `"123456"` | 密码，长度至少 6 位 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.userInfo | Object | 用户信息对象 |
| data.userInfo.id | Integer | 用户 ID |
| data.userInfo.username | String | 用户名 |
| data.userInfo.nickname | String | 用户昵称 |
| data.userInfo.email | String | 用户邮箱 |
| data.token | String | JWT 令牌，用于后续请求的身份验证 |

### 响应示例

#### 成功响应
```json
{
  "code": 20101,
  "msg": "登录成功",
  "data": {
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "admin",
      "email": "admin@example.com"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### 失败响应（用户不存在）
```json
{
  "code": 40101,
  "msg": "用户不存在",
  "data": null
}
```

#### 失败响应（密码错误）
```json
{
  "code": 40102,
  "msg": "用户名或密码错误",
  "data": null
}
```

---

## 1.2 用户注册

### 基本信息
- **接口名称**: 用户注册
- **请求方法**: POST
- **请求路径**: `/api/user/register`
- **功能描述**: 创建新用户账号，注册成功后返回用户信息

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| username | String | 是 | `"newuser"` | 用户名，3-6 位字母数字下划线，必须唯一 |
| email | String | 是 | `"user@example.com"` | 邮箱地址，标准邮箱格式，必须唯一 |
| password | String | 是 | `"123456"` | 密码，长度至少 6 位 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.id | Integer | 用户 ID |
| data.username | String | 用户名 |
| data.nickname | String | 用户昵称（默认与用户名相同） |
| data.email | String | 用户邮箱 |
| data.createdAt | DateTime | 注册时间戳 |

### 响应示例

#### 成功响应
```json
{
  "code": 20101,
  "msg": "操作成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "nickname": "newuser",
    "email": "user@example.com",
    "createdAt": "2026-03-23T10:30:00"
  }
}
```

#### 失败响应（用户名已存在）
```json
{
  "code": 40103,
  "msg": "用户名已存在",
  "data": null
}
```

#### 失败响应（邮箱已存在）
```json
{
  "code": 40104,
  "msg": "邮箱已被注册",
  "data": null
}
```

---

## 1.3 修改密码

### 基本信息
- **接口名称**: 修改密码
- **请求方法**: POST
- **请求路径**: `/api/user/change-password`
- **功能描述**: 修改用户密码，需要验证旧密码和用户身份信息

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| username | String | 是 | `"admin"` | 用户名，用于身份验证 |
| email | String | 是 | `"admin@example.com"` | 邮箱，用于身份验证 |
| oldPassword | String | 是 | `"123456"` | 旧密码，长度至少 6 位，用于验证身份 |
| newPassword | String | 是 | `"newpass"` | 新密码，长度至少 6 位，将替换旧密码 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | null | 修改密码无返回数据 |

### 响应示例

#### 成功响应
```json
{
  "code": 20101,
  "msg": "密码修改成功",
  "data": null
}
```

#### 失败响应（用户不存在）
```json
{
  "code": 40101,
  "msg": "用户不存在",
  "data": null
}
```

#### 失败响应（旧密码错误）
```json
{
  "code": 40107,
  "msg": "旧密码错误",
  "data": null
}
```

#### 失败响应（邮箱不匹配）
```json
{
  "code": 40105,
  "msg": "邮箱地址不正确",
  "data": null
}
```

---

## 1.4 退出登录

### 基本信息
- **接口名称**: 退出登录
- **请求方法**: POST
- **请求路径**: `/api/user/logout`
- **功能描述**: 用户退出登录（JWT 无状态认证，前端删除 Token 即可）

### 请求参数

无

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | null | 退出登录无返回数据 |

### 响应示例

#### 成功响应
```json
{
  "code": 20101,
  "msg": "操作成功",
  "data": null
}
```

---

# 二、分类模块 (CategoryController)

**基础路径**: `/api/category`

## 2.1 创建主标签

### 基本信息
- **接口名称**: 创建主标签
- **请求方法**: POST
- **请求路径**: `/api/category/main-tag`
- **功能描述**: 创建新的主标签（父标签 ID 为 0）

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| name | String | 是 | `"Java"` | 标签名称，1-6 位字符 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.id | Long | 主标签 ID |
| data.name | String | 主标签名称 |
| data.parentId | Long | 父标签 ID（固定为 0） |
| data.createdAt | DateTime | 创建时间戳 |

### 响应示例

#### 成功响应
```json
{
  "code": 20301,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "Java",
    "parentId": 0,
    "createdAt": "2026-03-23T10:30:00"
  }
}
```

#### 失败响应（标签名称已存在）
```json
{
  "code": 40302,
  "msg": "标签名称已存在",
  "data": null
}
```

---

## 2.2 创建子标签

### 基本信息
- **接口名称**: 创建子标签
- **请求方法**: POST
- **请求路径**: `/api/category/sub-tag`
- **功能描述**: 在指定主标签下创建新的子标签

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| parentId | Long | 是 | `1` | 父标签 ID，必须存在 |
| name | String | 是 | `"Spring"` | 标签名称，1-6 位字符，不能重复 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.id | Long | 子标签 ID |
| data.name | String | 子标签名称 |
| data.parentId | Long | 父标签 ID |
| data.createdAt | DateTime | 创建时间戳 |

### 响应示例

#### 成功响应
```json
{
  "code": 20301,
  "msg": "操作成功",
  "data": {
    "id": 2,
    "name": "Spring",
    "parentId": 1,
    "createdAt": "2026-03-23T10:30:00"
  }
}
```

#### 失败响应（父标签不存在）
```json
{
  "code": 40303,
  "msg": "父标签不存在",
  "data": null
}
```

#### 失败响应（标签名称已存在）
```json
{
  "code": 40302,
  "msg": "标签名称已存在",
  "data": null
}
```

---

## 2.3 删除标签

### 基本信息
- **接口名称**: 删除标签
- **请求方法**: DELETE
- **请求路径**: `/api/category/tag`
- **功能描述**: 删除指定的标签（主标签或子标签），采用软删除（设置 isDeleted=1）

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| name | String | 是 | `"Java"` | 标签名称 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | String/Object | 删除结果消息 |

### 响应示例

#### 成功响应（删除主标签）
```json
{
  "code": 20301,
  "msg": "操作成功",
  "data": "主标签删除成功"
}
```

#### 成功响应（删除子标签）
```json
{
  "code": 20301,
  "msg": "操作成功",
  "data": "小标签删除成功"
}
```

#### 失败响应（标签不存在）
```json
{
  "code": 40301,
  "msg": "标签不存在",
  "data": null
}
```

#### 失败响应（标签已被删除）
```json
{
  "code": 40304,
  "msg": "标签已被删除",
  "data": null
}
```

#### 失败响应（存在未删除的子标签）
```json
{
  "code": 40305,
  "msg": "该主标签下存在未删除的子标签，无法删除",
  "data": null
}
```

---

## 2.4 获取标签树

### 基本信息
- **接口名称**: 获取标签树
- **请求方法**: GET
- **请求路径**: `/api/category/tags/tree`
- **功能描述**: 获取所有未删除标签的树形结构，以主标签为根节点，子标签为叶子节点

### 请求参数

无

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Array | 标签树数组 |
| data[].id | Long | 标签 ID |
| data[].name | String | 标签名称 |
| data[].children | Array | 子标签数组（仅主标签有） |
| data[].children[].id | Long | 子标签 ID |
| data[].children[].name | String | 子标签名称 |
| data[].children[].children | null | 子标签无子节点 |

### 数据结构说明

**TagTreeNode（标签树节点）**:
- `id`: 标签 ID
- `name`: 标签名称
- `children`: 子节点列表（主标签包含子标签数组，子标签为 null）

### 响应示例

#### 成功响应
```json
{
  "code": 20301,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Java",
      "children": [
        {
          "id": 2,
          "name": "Spring",
          "children": null
        },
        {
          "id": 3,
          "name": "MyBatis",
          "children": null
        }
      ]
    },
    {
      "id": 4,
      "name": "Python",
      "children": [
        {
          "id": 5,
          "name": "Django",
          "children": null
        }
      ]
    }
  ]
}
```

---

# 三、文章模块 (ArticleController)

**基础路径**: `/api/article`

## 3.1 创建文章

### 基本信息
- **接口名称**: 创建文章
- **请求方法**: POST
- **请求路径**: `/api/article`
- **功能描述**: 创建新文章，支持关联主标签和多个子标签（最多 5 个）

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| userId | Long | 是 | `1` | 用户 ID |
| categoryId | Long | 是 | `1` | 分类 ID，必须是主标签（parent_id=0） |
| title | String | 是 | `"Spring Boot 入门教程"` | 文章标题，最多 200 字符 |
| summary | String | 是 | `"本文介绍 Spring Boot 的基础知识..."` | 文章概述，最多 500 字符 |
| content | String | 是 | `"# Spring Boot 入门\n\nSpring Boot 是..."` | 文章内容（Markdown 格式） |
| isTop | Integer | 否 | `0` | 是否置顶：0-否，1-是（默认 0） |
| isDraft | Integer | 否 | `0` | 是否草稿：0-已发布，1-草稿（默认 0） |
| isDeleted | Integer | 否 | `0` | 是否删除：0-否，1-是（默认 0） |
| scategoryId | String | 否 | `"2,3,5"` | 子标签 ID 字符串，逗号分隔，最多 5 个 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.id | Long | 文章 ID |
| data.userId | Long | 用户 ID |
| data.categoryId | Long | 主分类 ID |
| data.title | String | 文章标题 |
| data.summary | String | 文章概述 |
| data.content | String | 文章内容 |
| data.isTop | Integer | 置顶状态 |
| data.isDraft | Integer | 草稿状态 |
| data.isDeleted | Integer | 删除标记 |
| data.createdAt | DateTime | 创建时间 |
| data.updatedAt | DateTime | 更新时间 |
| data.userInfo | Object | 作者信息 |
| data.userInfo.username | String | 作者用户名 |
| data.userInfo.email | String | 作者邮箱 |
| data.scategoryId | String | 子标签 ID 字符串（逗号分隔） |

### 响应示例

#### 成功响应
```json
{
  "code": 20201,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "categoryId": 1,
    "title": "Spring Boot 入门教程",
    "summary": "本文介绍 Spring Boot 的基础知识...",
    "content": "# Spring Boot 入门\n\nSpring Boot 是...",
    "isTop": 0,
    "isDraft": 0,
    "isDeleted": 0,
    "createdAt": "2026-03-23T10:30:00",
    "updatedAt": "2026-03-23T10:30:00",
    "userInfo": {
      "username": "admin",
      "email": "admin@example.com"
    },
    "scategoryId": "2,3"
  }
}
```

#### 失败响应（分类不存在）
```json
{
  "code": 40204,
  "msg": "分类不存在",
  "data": null
}
```

#### 失败响应（分类不是主标签）
```json
{
  "code": 40205,
  "msg": "分类必须是主标签",
  "data": null
}
```

#### 失败响应（子标签数量超限）
```json
{
  "code": 40208,
  "msg": "子标签数量不能超过 5 个",
  "data": null
}
```

---

## 3.3 获取我的文章列表

### 基本信息
- **接口名称**: 获取我的文章列表
- **请求方法**: GET
- **请求路径**: `/api/article/my-list`
- **功能描述**: 分页获取当前登录用户的文章列表，支持按主标签和子标签筛选（需要登录）

### 请求参数

#### Query 参数

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| pageNum | Integer | 否 | `1` | 页码，默认 1 |
| pageSize | Integer | 否 | `10` | 每页数量，默认 10 |
| categoryIds | String | 否 | `"1,2,3"` | 大标签 ID 列表，逗号分隔 |
| categoryId | Long | 否 | `1` | 主标签 ID，单个，用于与子标签联动 |
| scategoryIds | String | 否 | `"2,3,4"` | 子标签 ID 列表，逗号分隔 |

#### 请求头
需要包含 JWT Token 进行身份验证（后端从 Request 上下文中自动获取 userId）

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.list | Array | 文章列表 |
| data.list[].id | Long | 文章 ID |
| data.list[].userId | Long | 用户 ID |
| data.list[].categoryId | Long | 主分类 ID |
| data.list[].title | String | 文章标题 |
| data.list[].summary | String | 文章概述 |
| data.list[].isTop | Integer | 置顶状态 |
| data.list[].updatedAt | DateTime | 更新时间 |
| data.list[].username | String | 作者用户名 |
| data.list[].categoryName | String | 分类名称 |
| data.list[].subCategories | Array | 子标签列表 |
| data.list[].subCategories[].id | Long | 子标签 ID |
| data.list[].subCategories[].name | String | 子标签名称 |
| data.total | Long | 总记录数 |
| data.pageNum | Integer | 当前页码 |
| data.pageSize | Integer | 每页数量 |
| data.totalPages | Integer | 总页数 |

### 数据结构说明

**SubCategoryInfo（子标签信息）**:
- `id`: 子标签 ID
- `name`: 子标签名称

### 响应示例

#### 成功响应
```json
{
  "code": 20201,
  "msg": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "categoryId": 1,
        "title": "Spring Boot 入门教程",
        "summary": "本文介绍 Spring Boot 的基础知识...",
        "isTop": 1,
        "updatedAt": "2026-03-23T10:30:00",
        "username": "admin",
        "categoryName": "Java",
        "subCategories": [
          {
            "id": 2,
            "name": "Spring"
          },
          {
            "id": 3,
            "name": "MyBatis"
          }
        ]
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

---

## 3.2 获取文章列表

### 基本信息
- **接口名称**: 获取文章列表
- **请求方法**: GET
- **请求路径**: `/api/article/list`
- **功能描述**: 分页获取文章列表，支持按主标签和子标签筛选

### 请求参数

#### Query 参数

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| pageNum | Integer | 否 | `1` | 页码，默认 1 |
| pageSize | Integer | 否 | `10` | 每页数量，默认 10 |
| categoryIds | String | 否 | `"1,2,3"` | 大标签 ID 列表，逗号分隔 |
| categoryId | Long | 否 | `1` | 主标签 ID，单个，用于与子标签联动 |
| scategoryIds | String | 否 | `"2,3,4"` | 子标签 ID 列表，逗号分隔 |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.list | Array | 文章列表 |
| data.list[].id | Long | 文章 ID |
| data.list[].userId | Long | 用户 ID |
| data.list[].categoryId | Long | 主分类 ID |
| data.list[].title | String | 文章标题 |
| data.list[].summary | String | 文章概述 |
| data.list[].isTop | Integer | 置顶状态 |
| data.list[].updatedAt | DateTime | 更新时间 |
| data.list[].username | String | 作者用户名 |
| data.list[].categoryName | String | 分类名称 |
| data.list[].subCategories | Array | 子标签列表 |
| data.list[].subCategories[].id | Long | 子标签 ID |
| data.list[].subCategories[].name | String | 子标签名称 |
| data.total | Long | 总记录数 |
| data.pageNum | Integer | 当前页码 |
| data.pageSize | Integer | 每页数量 |
| data.totalPages | Integer | 总页数 |

### 数据结构说明

**SubCategoryInfo（子标签信息）**:
- `id`: 子标签 ID
- `name`: 子标签名称

### 响应示例

#### 成功响应
```json
{
  "code": 20201,
  "msg": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "categoryId": 1,
        "title": "Spring Boot 入门教程",
        "summary": "本文介绍 Spring Boot 的基础知识...",
        "isTop": 1,
        "updatedAt": "2026-03-23T10:30:00",
        "username": "admin",
        "categoryName": "Java",
        "subCategories": [
          {
            "id": 2,
            "name": "Spring"
          },
          {
            "id": 3,
            "name": "MyBatis"
          }
        ]
      },
      {
        "id": 2,
        "userId": 1,
        "categoryId": 1,
        "title": "Spring MVC 详解",
        "summary": "深入解析 Spring MVC 框架...",
        "isTop": 0,
        "updatedAt": "2026-03-23T11:00:00",
        "username": "admin",
        "categoryName": "Java",
        "subCategories": [
          {
            "id": 2,
            "name": "Spring"
          }
        ]
      }
    ],
    "total": 15,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 2
  }
}
```

---

## 3.4 删除文章

### 基本信息
- **接口名称**: 删除文章
- **请求方法**: DELETE
- **请求路径**: `/api/article/{id}`
- **功能描述**: 删除指定文章（需要登录权限）

### 请求参数

#### 路径参数

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| id | Long | 是 | `1` | 文章 ID |

#### 请求头
需要包含 JWT Token 进行身份验证

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | null | 删除文章无返回数据 |

### 响应示例

#### 成功响应
```json
{
  "code": 20201,
  "msg": "操作成功",
  "data": null
}
```

#### 失败响应（文章不存在）
```json
{
  "code": 40201,
  "msg": "文章不存在",
  "data": null
}
```

#### 失败响应（无权访问）
```json
{
  "code": 40203,
  "msg": "无权访问文章",
  "data": null
}
```

---

## 3.5 更新文章置顶状态

### 基本信息
- **接口名称**: 更新文章置顶状态
- **请求方法**: PUT
- **请求路径**: `/api/article/top`
- **功能描述**: 更新文章的置顶状态（需要登录权限）

### 请求参数

#### Body 参数（JSON）

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| articleId | Long | 是 | `1` | 文章 ID |
| isTop | Integer | 是 | `1` | 置顶状态：0-否，1-是 |

#### 请求头
需要包含 JWT Token 进行身份验证

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | null | 更新操作无返回数据 |

### 响应示例

#### 成功响应
```json
{
  "code": 20201,
  "msg": "操作成功",
  "data": null
}
```

#### 失败响应（文章不存在）
```json
{
  "code": 40201,
  "msg": "文章不存在",
  "data": null
}
```

#### 失败响应（无权访问）
```json
{
  "code": 40203,
  "msg": "无权访问文章",
  "data": null
}
```

---

## 3.6 获取文章详情

### 基本信息
- **接口名称**: 获取文章详情
- **请求方法**: GET
- **请求路径**: `/api/article/{id}`
- **功能描述**: 获取单篇文章的完整详情信息（无需登录）

### 请求参数

#### 路径参数

| 字段名 | 类型 | 必填 | 示例值 | 说明 |
|--------|------|------|--------|------|
| id | Long | 是 | `1` | 文章 ID |

### 响应参数

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码（5 位数） |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.id | Long | 文章 ID |
| data.userId | Long | 用户 ID |
| data.categoryId | Long | 主分类 ID |
| data.title | String | 文章标题 |
| data.summary | String | 文章概述 |
| data.content | String | 文章内容 |
| data.isTop | Integer | 置顶状态 |
| data.isDraft | Integer | 草稿状态 |
| data.isDeleted | Integer | 删除标记 |
| data.createdAt | DateTime | 创建时间 |
| data.updatedAt | DateTime | 更新时间 |
| data.username | String | 作者用户名 |
| data.categoryName | String | 分类名称 |
| data.subCategories | Array | 子标签列表 |
| data.subCategories[].id | Long | 子标签 ID |
| data.subCategories[].name | String | 子标签名称 |

### 数据结构说明

**SubCategoryInfo（子标签信息）**:
- `id`: 子标签 ID
- `name`: 子标签名称

### 响应示例

#### 成功响应
```json
{
  "code": 20201,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "categoryId": 1,
    "title": "Spring Boot 入门教程",
    "summary": "本文介绍 Spring Boot 的基础知识...",
    "content": "# Spring Boot 入门\n\nSpring Boot 是...",
    "isTop": 1,
    "isDraft": 0,
    "isDeleted": 0,
    "createdAt": "2026-03-20T10:00:00",
    "updatedAt": "2026-03-23T10:30:00",
    "username": "admin",
    "categoryName": "Java",
    "subCategories": [
      {
        "id": 2,
        "name": "Spring"
      },
      {
        "id": 3,
        "name": "MyBatis"
      }
    ]
  }
}
```

#### 失败响应（文章不存在）
```json
{
  "code": 40201,
  "msg": "文章不存在",
  "data": null
}
```

---

# 四、数据结构说明

## DTO 数据结构

### 1. SubCategoryInfo（子标签信息）

用于文章列表和详情中返回子标签信息。

```java
{
  "id": Long,        // 子标签 ID
  "name": String     // 子标签名称
}
```

### 2. TagTreeNode（标签树节点）

用于标签树接口返回树形结构数据。

```java
{
  "id": Long,                    // 标签 ID
  "name": String,                // 标签名称
  "children": List<TagTreeNode>  // 子节点列表（主标签包含子标签，子标签为 null）
}
```

### 3. ApiResponse（统一响应格式）

所有接口的统一响应格式。

```java
{
  "code": Integer,    // 5 位业务状态码
  "msg": String,      // 响应消息
  "data": T           // 响应数据（可能为 null）
}
```

---

# 五、错误码说明

## 错误码格式规范

错误码采用 5 位数格式：**XYYZZ**
- **第 1 位 (X)**: 状态类型（2=成功，4=客户端错误，5=服务端错误）
- **第 2-3 位 (YY)**: 模块标识（01=用户模块，02=文章模块，03=分类模块）
- **第 4-5 位 (ZZ)**: 具体操作序号（从 01 开始）

## 错误码列表

### 用户模块错误码 (401XX / 501XX)

#### 成功状态码 (201XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 20101 | 操作成功 | 所有操作成功时返回 |

#### 客户端错误 (401XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 40101 | 用户不存在 | 登录或修改密码时用户不存在 |
| 40102 | 用户名或密码错误 | 登录时凭据验证失败 |
| 40103 | 用户名已存在 | 注册时用户名重复 |
| 40104 | 邮箱已存在 | 注册时邮箱重复 |
| 40105 | 参数无效 | 修改密码时邮箱不匹配等参数问题 |
| 40106 | 未授权访问 | 未登录或权限不足时使用 |
| 40107 | 旧密码错误 | 修改密码时旧密码验证失败 |
| 40108 | 账号已被禁用 | 用户账号被禁用时登录 |
| 40109 | Token 无效或已过期 | Token 验证失败时使用 |

#### 服务端错误 (501XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 50101 | 服务器内部错误 | 通用服务端错误 |
| 50102 | 数据库错误 | 数据库操作失败 |
| 50103 | 生成 Token 失败 | JWT Token 生成失败 |
| 50104 | 加密失败 | 密码加密或校验失败 |

---

### 文章模块错误码 (402XX / 502XX)

#### 成功状态码 (202XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 20201 | 操作成功 | 所有操作成功时返回 |

#### 客户端错误 (402XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 40201 | 文章不存在 | 文章 ID 不存在 |
| 40202 | 文章已被删除 | 访问已删除的文章 |
| 40203 | 无权访问文章 | 无权限操作该文章 |
| 40204 | 分类不存在 | 分类 ID 不存在 |
| 40205 | 分类必须是主标签 | 文章的分类不是主标签 |
| 40206 | 子标签不存在 | 子标签 ID 不存在 |
| 40207 | 子标签不能是主标签 | 子标签 ID 实际是主标签 |
| 40208 | 子标签数量超过限制 | 子标签数量超过 5 个 |
| 40209 | 参数无效 | 请求参数格式错误 |
| 40210 | 置顶状态无效 | 置顶状态值不合法 |

#### 服务端错误 (502XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 50201 | 服务器内部错误 | 通用服务端错误 |
| 50202 | 数据库错误 | 数据库操作失败 |

---

### 分类模块错误码 (403XX / 503XX)

#### 成功状态码 (203XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 20301 | 操作成功 | 所有操作成功时返回 |

#### 客户端错误 (403XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 40301 | 分类不存在 | 分类/标签不存在 |
| 40302 | 分类名称已存在 | 标签名称重复 |
| 40303 | 父标签不存在 | 创建子标签时父标签不存在 |
| 40304 | 标签已被删除 | 操作的标签已被删除 |
| 40305 | 存在未删除的子标签 | 主标签下有未删除的子标签 |
| 40306 | 参数无效 | 请求参数格式错误 |

#### 服务端错误 (503XX)
| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 50301 | 服务器内部错误 | 通用服务端错误 |
| 50302 | 数据库错误 | 数据库操作失败 |

---

## 附录

### 统一响应格式

所有接口均使用统一的响应格式 `ApiResponse<T>`：

```json
{
  "code": 20101,
  "msg": "操作成功",
  "data": {}
}
```

- **code**: 5 位业务状态码
- **msg**: 响应消息
- **data**: 响应数据（可能为 null）

### 注意事项

1. **身份验证**: 除登录、注册接口外，其他接口需要在请求头中携带 JWT Token
2. **数据格式**: 所有请求和响应均采用 JSON 格式
3. **字符编码**: 使用 UTF-8 编码
4. **时间格式**: 日期时间使用 ISO 8601 格式（如：`2026-03-23T10:30:00`）
5. **密码安全**: 所有密码在传输和存储时均使用 BCrypt 加密
6. **标签规则**: 
   - 主标签：parent_id = 0
   - 子标签：parent_id ≠ 0（指向对应的主标签 ID）
   - 标签名称长度：1-6 位字符
   - 子标签数量：每篇文章最多关联 5 个子标签

### 跨域说明

所有控制器均已配置允许跨域访问（`@CrossOrigin(origins = "*")`），支持前后端分离开发。

### JWT Token 使用说明

#### Token 获取
通过登录接口（`POST /api/user/login`）获取 JWT Token。

#### Token 使用
需要在需要认证的接口请求头中添加 Token：
```
Authorization: Bearer <your-token-here>
```

或者在请求体中传递 `userId`（具体取决于拦截器的实现）。

#### Token 有效期
Token 有效期由 JWT 配置决定，过期后需要重新登录。

---

**文档版本**: v3.0  
**最后更新**: 2026-03-25  
**技术栈**: Spring Boot 4.0.3 + Spring Security + JPA + MySQL + JWT (JJWT 0.11.5)
