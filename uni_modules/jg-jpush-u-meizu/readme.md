# jg-jpush-u-meizu

## 1. 特别说明
这个插件是依附：jg-jpush-u 插件而存在，要使用这个插件就必须使用jg-jpush-u插件

## 2. 项目配置

### 2.1 配置manifestPlaceholders.json

在 `nativeResources/android/` 目录下创建或修改 `manifestPlaceholders.json` 文件：

```json
{
    "MEIZU_APPKEY": "MZ-您的应用对应的魅族推送配置",
    "MEIZU_APPID": "MZ-您的应用对应的魅族 APP ID"
}
```

> **⚠️ 重要：`MEIZU_APPKEY`、`MEIZU_APPID` 两项的值必须加 `MZ-` 前缀**
>
> 例如 APP ID 是 `123456`，这里要填 `MZ-123456`。极光 SDK 读取时会自动去掉前 3 个字符的前缀。
>
> 原因：`manifestPlaceholders.json` 里的值只是做**文本替换**写进 AndroidManifest，JSON 的引号不会传递过去。
> 当值是纯数字时（厂商 APP ID 通常就是纯数字），aapt2 会把它编译成 **整型（int）** 而不是字符串，
> SDK 用 `Bundle.getString()` 就会读到 `null`，表现为"清单文件里明明配了却读不到"，厂商通道注册失败、拿不到厂商 token。
> 加上非数字的前缀后，aapt2 就会按字符串编译，问题不再出现。


**参数说明：**
- `MEIZU_APPKEY`: 魅族推送服务配置信息，需加 `MZ-` 前缀
- `MEIZU_APPID`: 魅族平台注册的APP ID，需加 `MZ-` 前缀（APP ID 是纯数字）

**示例配置：**
```json
{
  "MEIZU_APPKEY": "MZ-your-meizu-push-config",
  "MEIZU_APPID": "MZ-123456"
}
```

## 3. 代码集成

### 3.1 引入插件(必须)

- 只需在你的代码中引入插件即可
- 为了解决名字冲突，最好重命名init：init as 不冲突的名字

```typescript
import { 
  init as initMeizu
} from "@/uni_modules/jg-jpush-u-meizu"
```

## 4. 注意事项

- 确保已在魅族开发者平台配置推送服务
- 确保已正确配置manifestPlaceholders.json文件
- 配置值必须加 `MZ-` 前缀，否则纯数字的 APP ID 会被编译成整型导致 SDK 读取失败（详见 2.1 节说明）
- 该插件仅支持Android平台
- 需要配合jg-jpush-u主插件使用

## 5. 开发文档
[UTS 语法](https://uniapp.dcloud.net.cn/tutorial/syntax-uts.html)
[UTS API插件](https://uniapp.dcloud.net.cn/plugin/uts-plugin.html)
[UTS uni-app兼容模式组件](https://uniapp.dcloud.net.cn/plugin/uts-component.html)
[UTS 标准模式组件](https://doc.dcloud.net.cn/uni-app-x/plugin/uts-vue-component.html)
[Hello UTS](https://gitcode.net/dcloud/hello-uts)