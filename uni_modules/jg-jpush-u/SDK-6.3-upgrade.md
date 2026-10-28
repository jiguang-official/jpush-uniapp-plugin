# 1.3.2 SDK 升级与公开接口

候选版本1.3.2遵循本仓更新脚本的patch规则，尚未发布。Android JPush6.3.0（JCore由Maven传递依赖解析，当前核验5.6.0）；iOS内嵌官方JPush6.3.0/JCore5.6.0 device arm64静态库；Harmony @jg/push1.5.0。旧JG-JPush插件线不在本次范围。

## 接口与事件矩阵

| 接口/字段 | Android | iOS | Harmony | 契约 |
| --- | --- | --- | --- | --- |
| requestSubscribeChannels(string[]) | 新增，6.3公开接口 | 不支持 | 不支持 | Promise<void>仅确认派发；1–3个唯一非空channelId，需当前前台Activity；最终onCommandResult cmd2013 |
| cmd2013 extra/commandData/channels | 新增递归保型 | 不适用 | 不适用 | 数值/布尔/数组/对象/null保留；无extra为空对象，cmd/errorCode/msg保留；过滤intent_字段，限制递归16层；旧2012行为不变 |
| turnOffPush() | 补齐现有UPS能力 | 补齐6.2.2起能力 | 补齐1.4.2起能力 | Promise<PushServiceResult>；SDK code为业务结果，非0不伪装成功；每次请求独立；原stopPush保留 |
| turnOnPush() | SDK回调code+dispatched | 仅dispatched | 仅dispatched | iOS/Harmony无原生完成回调，不宣称注册/连接成功；原resumePush保留 |
| initCrashHandler()/stopCrashHandler() | 显式成对入口 | 不支持 | 不支持 | Promise<void>，不由init自动启用，不人为制造崩溃 |
| setLiveActivityTokenReportEnabled(boolean) | 不支持 | 补齐6.3公开诊断开关 | 不支持 | Promise<void>；true/false均直传，仅控制诊断埋点；服务端LiveActivity开关独立 |
| registerLiveActivityPushToken(params) | 不支持 | 补齐原生已有接口（6.2.0基线已存在） | 不支持 | iOS16.1+；activityId非空，token为偶数长度hex或null解绑，sequence有符号32位整数 |
| registerLiveActivityPushToStartToken(params) | 不支持 | 补齐原生已有接口（6.2.0基线已存在） | 不支持 | iOS17.2+；activityAttributes/token/sequence规则同上 |
| getRegistrationIdAsync() | 包装原同步值 | 包装原同步值 | 等待SDK真实结果并更新缓存 | Promise<string>；空值不表示注册成功 |
| isPushStoppedAsync() | 原生同步状态 | 不支持，沿用getPushStatus callback | SDK真实异步状态 | Promise<boolean>；Harmony undefined拒绝STATUS_UNAVAILABLE，不当成false |
| initAsync(context) | 不支持 | 不支持 | 等待原生初始化 | Promise<void>；初始化完成不等于RID已注册；原void init观察失败并发同一listener的onBridgeError |
| clearUpgradeCallbacks() | 代际清理UPS回调 | 代际清理UPS/LiveToken回调 | 失效异步查询并dispose事件listener | 页面/业务owner销毁前显式调用；示例onUnload已接入 |
| onVoipMessage.voipSupportStatus | 新增原始整数 | 不适用 | 不适用 | 不猜布尔含义，保留未知整数，不记录载荷日志 |
| onJMessageVoIP.callId | 不适用 | 不适用 | JSON原样保留 | 同时保留通知channel/notificationType等公开字段；不将内部协议/SPI暴露为JS接口 |

不支持平台的新增Promise方法拒绝Error('UNSUPPORTED_PLATFORM')。参数、生命周期和环境错误分别为INVALID_ARGUMENT、BRIDGE_INVALIDATED、NO_ACTIVITY/NO_CONTEXT、UNSUPPORTED_OS、NATIVE_EXCEPTION等。LiveToken返回{code,activityId,token,sequence}，token转小写hex或null；业务code保持原值。

## 生命周期与兼容

clearUpgradeCallbacks使已排队旧请求在调用SDK前拒绝BRIDGE_INVALIDATED；已派发但尚未完成的Android/iOS请求释放页面闭包，迟到/重复SDK回调被丢弃，销毁后不再结算该页面的Promise。调用方必须把这些Promise与页面生命周期一起结束，不能在销毁后的await继续操作页面。新generation请求不被旧排队清理删除。此清理是模块级owner操作；多个页面共享插件时由共同业务owner统一管理，不能由任意非owner页面清理其他页面请求。

Android另接入真实Activity destroy回调；iOS没有假造终止hook，页面/宿主owner须显式调用。Harmony替换listener会dispose旧引用；旧init/缓存查询错误只对当时generation和callback身份可见，不向新页面发旧错误。Harmony旧同步RID/状态getter仍为缓存（初始空字符串/false），调用即后台刷新，需实时结果用Async版本；UPS业务结果返回后刷新失败不覆盖原code/msg，仅向同一listener报告onBridgeError，clear期间则拒绝BRIDGE_INVALIDATED。

```ts
import { turnOffPush, requestSubscribeChannels, clearUpgradeCallbacks } from '@/uni_modules/jg-jpush-u'
import { onUnload } from '@dcloudio/uni-app'
onUnload(() => { clearUpgradeCallbacks() })
// Android；捕获参数/环境错误，cmd2013才是最终订阅结果。
requestSubscribeChannels(['channel-id']).catch(error => console.warn(error.message))
turnOffPush().then(result => { /* 检查原生业务code */ }).catch(error => console.warn(error.message))
// iOS解绑：registerLiveActivityPushToken({activityId:'activity',token:null,sequence:1})
```

## 厂商与二进制

同仓config.json列出的7款仍维护厂商模块均同步正式6.3.0适配包：FCM/Honor/Huawei/Meizu/OPPO/Vivo/Xiaomi；各模块单独patch版本及日志已更新。真实AAR公开类均核验unregister(Context):boolean，作为SDK内部SPI兼容检查，不向业务暴露。已停更NIO保留6.1.0，未捏造新包。使用方仅集成所需厂商模块，配置AppID/权限及厂商设备验收仍需自行完成。

iOS库取自官方6.3.0 zip，device arm64切片；隐私清单合并同包JPush/JCore正式manifest条目，未采用noidfa二进制冒充标准包。此仓原有静态库布局只含真机arm64，不宣称支持模拟器。验证时临时宿主采用官方同版模拟器切片仅用于编译/链接检查，不改变发行二进制布局。最低系统/权限声明保持原有；LiveActivity新入口有系统版本保护。混编Java/Swift需要支持UTS原生混编的HBuilderX（官方4.25起），已有uni-app/uni-app-x声明4.75起；本轮实际编译工具5.15。本轮HBuilderX engines由^3.6.8调整为^4.25以声明新增混编的真实最低要求；uni-app/uni-app-x既有^4.75要求保持。只实测5.15，未声明4.25或最低系统已实际构建通过。

## 验证与发布边界

- HBuilderX5.15真实生成Android Kotlin，Kotlin2.2对实际DCloud runtime和正式SDK6.3/5.6原生类型编译通过。5项Robolectric测试运行实际生成Kotlin/Java，覆盖多旧请求清理、新代保留、迟到重复回调及null command extra与嵌套类型。
- Harmony实际UTS源码11项mock契约通过；无DevEco/hvigor/原生SDK构建环境，未执行ArkTS/HAP构建。用户豁免Harmony真机。
- Swift codec可执行边界测试及100次并发代际失效通过；HBuilder iOS UTS原生编译/链接用官方同版模拟器切片通过。未执行随包device切片完整真机宿主构建/运行。
- Android完整APK/真机仍待匹配合法DCloud自定义基座或官方离线工程配置；未拿标准基座或其他插件的消息结果替代，尚未发送UniApp验证消息。后台/冷启/厂商/UPS/VoIP真实行为均未外推通过。
- 当前仅源码PR，不自动HBuilder上传、tag、DCloud发布。模块产物排除测试、unpackage、离线SDK、签名与真实测试身份。

来源：本仓CLAUDE.md与update-sdk配置/脚本；[Android版本记录](https://docs.jiguang.cn/jpush/jpush_changelog/updates_Android)、[iOS版本记录](https://docs.jiguang.cn/jpush/jpush_changelog/updates_iOS)、[Harmony正式包](https://ohpm.openharmony.cn/ohpm/@jg%2Fpush)、正式Maven POM/AAR、官方iOS发行包和公开头文件；[DCloud混编规则](https://doc.dcloud.net.cn/uni-app-x/plugin/uts-plugin-hybrid.html)。
