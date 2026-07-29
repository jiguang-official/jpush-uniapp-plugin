# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是极光推送（JPush）的 uni-app / uni-app-x UTS 插件仓库（jpush-harmony-uniapp-plugin），包含 9 个发布到 DCloud 插件市场的插件，支持 Android / iOS / HarmonyOS 三端。仓库根目录同时是一个 uni-app demo 工程（`App.vue`、`pages/`、`manifest.json`），用作插件的宿主测试项目。

没有传统的构建/测试命令：插件通过 HBuilderX 编译运行，发布方式是手动将 `uni_modules/` 下各插件目录上传到 DCloud 插件市场。

## 插件结构

`uni_modules/` 下共 9 个插件：

- **jg-jpush-u** — 主插件，包含全部 API 实现
- **jg-jpush-u-{fcm,huawei,honor,meizu,oppo,vivo,xiaomi}** — 7 个仍受支持的厂商通道子模块，内容很薄：仅 `app-android/config.json`（Maven 依赖 `cn.jiguang.sdk.plugin:<厂商>:<版本>`）加一个几乎空的 `index.uts`
- **jg-jpush-u-nio** — 历史兼容模块；JPush Android 6.1.0 起官方停止支持 NIO SDK，不参与后续 SDK 更新与发布

主插件 `uni_modules/jg-jpush-u/utssdk/` 的三端结构：

| 文件/目录 | 作用 | SDK 引入方式 |
|---|---|---|
| `interface.uts` | 三端统一接口/类型定义 | — |
| `app-android/index.uts` | Android 实现 | `config.json` 中 Maven 依赖 `cn.jiguang.sdk:jpush:<版本>` |
| `app-ios/index.uts` | iOS 实现 | 直引静态库 `app-ios/Libs/` 下的 JPush/JCore .a 文件及头文件（版本号在库文件本身，不在 config.json） |
| `app-harmony/index.uts` | 鸿蒙实现 | `config.json` 中 ohpm 依赖 `@jg/push` |

修改 API 时需同时维护 `interface.uts` 与各端 `index.uts`。当某端 Changelog 出现新 API 时，先检查另一端是否已有等价实现，能合并的在 `interface.uts` 中合并为统一接口，确认无等价功能才标注单端 Only。

其他目录：

- `harmony-configs/entry/` — 鸿蒙宿主工程配置（module.json5、推送相关 Ability）
- `other_docs/` — 最佳实践文档（jpush 与 uni-unimp 联动）
- 集成/API 文档在 `uni_modules/jg-jpush-u/` 下：`Android_Integration_Guide.md`、`iOS_Integration_Guide.md`、`readme_me.md`（鸿蒙）、三端 API 文档及 `API_Comparison_Documentation.md`

## SDK 版本升级流程（update-sdk skill）

升级 JPush SDK 版本统一走 `.claude/skills/update-sdk/` 这个 skill（`.cursor/commands/update_sdk.md` 是 Cursor 侧的等价命令）。流程由 `.claude/skills/update-sdk/scripts/config.json` 驱动，该文件是子模块列表、依赖前缀、文件路径的唯一权威来源——**不要硬编码子模块列表**，必须从它动态读取。

脚本一览（python3，依赖 requests + beautifulsoup4）：

```bash
# 拉取极光官网 Changelog（结果缓存在 scripts/.changelog_cache.json）
python3 .claude/skills/update-sdk/scripts/changelog_fetcher.py --android <版本> --ios <版本>

# 更新主模块 Android config.json 版本号 + bump 插件版本 + 写 changelog
python3 .claude/skills/update-sdk/scripts/plugin_updater.py --android <版本> --bump-patch --changelog-summary "<摘要>"

# 下载并替换 iOS .a 静态库（可用 --ios-sdk-path 指定本地 SDK）
python3 .claude/skills/update-sdk/scripts/sdk_downloader.py --ios <版本>

# 打印需上传 DCloud 的路径，并完成 git commit/tag/push
python3 .claude/skills/update-sdk/scripts/publisher.py
```

## 版本与 changelog 约定

- 主模块与子模块版本号均在各自 `package.json` 的 `version` 字段，升级时 bump patch（3.9.9 → 4.0.0 这种进位也算 patch 规则）
- 每个插件有自己的 `changelog.md`，新条目插在文件**最顶部**，格式：

```
## <新版本号>（YYYY-MM-DD）
<变更说明>
```

- 子模块升级 SDK 时三件事配套完成：改 `app-android/config.json` 依赖版本、bump `package.json`、顶插 `changelog.md` 条目
