# Simple Sit

`Simple Sit` 是一个面向 Fabric 服务端的极简坐下模组，允许玩家通过右键或命令在常见方块上坐下，不需要复杂的 UI，也不要求玩家拥有 OP 权限。

English documentation: [README_EN.md](README_EN.md)

## 特性

- 右键地毯坐下
- 右键楼梯坐下
- 可选支持台阶与床
- 使用隐藏座位实体实现坐下，不残留可见实体
- 同一坐标自动占用检测，避免多人重叠坐下
- 潜行起身
- 玩家尝试移动或跳跃时自动起身
- `/sit` 原地坐下
- 管理员通过命令和配置文件控制功能开关
- 服务端优先设计，多人服务器通常只需要服务端安装

## 依赖

安装本模组时需要同时安装：

- `Fabric API`
- `Fabric Language Kotlin`

## 安装

### 多人服务器

将以下文件放入服务端 `mods` 目录：

- `simple-sit-<版本>-fabricmc<游戏版本>.jar`
- `fabric-api`
- `fabric-language-kotlin`

客户端通常不需要安装本模组即可连接和使用。

### 单人游戏

单人模式本质上是内置服务器，因此需要在本地客户端同时安装本模组和依赖。

## 使用方式

### 玩家操作

- 右键支持的方块坐下
- 按 `Shift` 起身
- 尝试移动或跳跃时自动起身
- 输入 `/sit` 原地坐下

### 支持的方块

默认支持：

- 楼梯
- 地毯
- 台阶
- 床

管理员可以按需关闭其中任意一种。

## 命令

### 玩家命令

| 命令 | 说明 |
| --- | --- |
| `/sit` | 在当前位置坐下 |

### 管理员命令

| 命令 | 说明 |
| --- | --- |
| `/sitadmin reload` | 重新加载配置文件 |
| `/sitadmin set enabled <true|false>` | 开关模组 |
| `/sitadmin set requireSneakRightClick <true|false>` | 是否必须潜行右键才能坐下 |
| `/sitadmin set requireEmptyHand <true|false>` | 是否必须空手右键 |
| `/sitadmin set allowStairs <true|false>` | 是否允许楼梯坐下 |
| `/sitadmin set allowCarpets <true|false>` | 是否允许地毯坐下 |
| `/sitadmin set allowSlabs <true|false>` | 是否允许台阶坐下 |
| `/sitadmin set allowBeds <true|false>` | 是否允许床坐下 |
| `/sitadmin set allowCommandSit <true|false>` | 是否允许 `/sit` |

## 配置文件

配置文件路径：

`config/simple-sit.json`

默认内容：

```json
{
  "enabled": true,
  "requireSneakRightClick": false,
  "requireEmptyHand": true,
  "allowStairs": true,
  "allowCarpets": true,
  "allowSlabs": true,
  "allowBeds": true,
  "allowCommandSit": true
}
```

## 设计说明

- 使用隐藏 `ArmorStand` 作为 Seat 实体，不额外注册网络实体，兼容性更稳
- 坐下逻辑运行在服务端，适合多人服务器统一控制
- 右键坐下默认要求主手交互，减少与其他行为冲突
- 提供 `requireEmptyHand` 和 `requireSneakRightClick` 两个配置项，方便服主快速调整误触策略

## 构建

要求：

- Java 21

构建命令：

```powershell
.\gradlew.bat build
```

默认产物文件名示例：

```text
simple-sit-1.0.0-fabricmc1.21.11.jar
```

## Release 发布

仓库支持两种 GitHub Release 发布方式：

### 通过 tag 自动发布

```bash
git tag v1.0.0
git push origin v1.0.0
```

### 手动触发发布

在 GitHub 的 `Actions` 页面中运行 `release` 工作流，并填写目标 tag。

## 开发状态

当前版本已完成以下基础能力：

- 坐下交互
- Seat 自动清理
- 管理员配置
- GitHub Actions 构建
- GitHub Release 自动发布
