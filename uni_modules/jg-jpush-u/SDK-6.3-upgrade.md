# 1.3.2 SDK 升级与公开接口

1.3.2：Android JPush6.3.0（JCore5.6.0由Maven传递依赖解析）；iOS内嵌官方JPush6.3.0/JCore5.6.0 device arm64静态库；Harmony @jg/push1.5.0。旧JG-JPush插件线独立维护。

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

7个维护中的厂商模块（FCM、Honor、Huawei、Meizu、OPPO、Vivo、Xiaomi）使用6.3.0配套构件，模块版本为1.2.5。NIO模块保持1.2.3，原生构件保持6.1.0。使用方仅集成所需厂商模块，并完成相应AppID、权限和厂商通道配置。

iOS随包原生库为官方JPush6.3.0/JCore5.6.0标准版device arm64静态库，包含对应隐私清单，不支持iOS Simulator。LiveActivity入口包含系统版本判断。Java/Swift混编要求HBuilderX 4.25及以上，uni-app/uni-app-x要求4.75及以上；实际部署版本还需同时满足所用框架及原生SDK的系统要求。

## 集成要求

- 为应用配置匹配的JPush AppKey、包名/Bundle ID、签名、通知权限及所需厂商通道；初始化完成不表示已注册或具备离线送达能力。
- 鸿蒙应用需具备PushKit开放能力及匹配的签名/推送配置。查询状态不可用时处理STATUS_UNAVAILABLE；停用返回非零code时按业务失败处理，恢复的dispatched结果不代表已经连接。
- LiveActivity须使用实际系统提供的Token；诊断上报开关与服务端LiveActivity开关分别配置。
- 多页面共享插件时，由共同业务owner管理clearUpgradeCallbacks，避免清理其他活跃页面的请求。

参考：[Android版本记录](https://docs.jiguang.cn/jpush/jpush_changelog/updates_Android)、[iOS版本记录](https://docs.jiguang.cn/jpush/jpush_changelog/updates_iOS)、[Harmony正式包](https://ohpm.openharmony.cn/ohpm/@jg%2Fpush)、正式Maven POM/AAR、官方iOS发行包和公开头文件；[DCloud混编规则](https://doc.dcloud.net.cn/uni-app-x/plugin/uts-plugin-hybrid.html)。
