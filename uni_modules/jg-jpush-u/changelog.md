## 1.3.1（2026-07-29）
Android/iOS JPush SDK 升级至 6.2.0、JCore SDK 升级至 5.5.0；Android 更新厂商通道并新增小米订阅消息接口 requestSubscribeChannel
## 1.3.0（2026-06-29）
鸿蒙补充通知送达回调 onNotifyMessageArrived 与通知未展示回调 onNotifyMessageUnShow（事件名与 Android/iOS 对齐），修复极光通道收到通知不触发送达回调的问题
## 1.2.9（2026-06-12）
鸿蒙 getRegistrationId/isPushStopped 恢复与 Android 一致的同步返回（内部缓存底层异步结果），注册完成前 getRegistrationId 可能返回空字符串
## 1.2.8（2026-06-12）
鸿蒙 setUserRequestNotificationPermission 适配 SDK 新签名，内部传入 applicationContext
## 1.2.7（2026-06-12）
HarmonyOS JPush SDK 升级至 1.4.0：鸿蒙新增 getPushStatus、setContext 接口，getRegistrationId/isPushStopped 改为异步返回 Promise，setClickWant 支持冷启动传入 context
## 1.2.6 (2026-05-15)
Android/iOS JPush SDK 升级至 6.1.0，新增 setKeepLongConnInBackground 及 VOIP 消息回调，更新厂商推送 SDK 版本
## 1.2.5（2026-05-13）
解决iOS偶现上报不了devicetoken的问题
## 1.2.4（2026-02-06）
添加requestRequiredPermission和requestPermission 问题处理
## 1.2.3（2026-01-29）
1. update ios sdk 6.0.0
2. iOS 新增 getPushStatus 接口
## 1.2.2（2026-01-22）
android 更新到6.0.1
## 1.2.1（2026-01-22）
android 更新到6.0.1
## 1.2.0（2026-01-22）
android 升级到6.0.0
## 1.1.9（2025-12-09）
代码上传
## 1.1.8（2025-12-08）
android 不回调问题
## 1.1.7（2025-12-08）
iOS ：1. setBadgeNumber 和 resetBadge 接口同时会调用极光接口设置角标和调用系统接口设置本地角标。2. 新增getBadgeNumber接口获取当前角标数量。
## 1.1.6（2025-12-08）
鸿蒙端冷启动没有点击回调
## 1.1.5（2025-12-04）
解决iOS点击通知冷启动app不回调通知回调的问题
## 1.1.4（2025-11-06）
android 兼容
## 1.1.3（2025-10-22）
ios sdk 更新到 jpush5.9.0
## 1.1.2（2025-10-13）
android支持动态设置appkey
## 1.1.1（2025-08-05）
更改支持版本信息
## 1.1.0（2025-07-29）
添加android和ios支持
## 1.0.2（2025-04-24）
兼容问题优化
## 1.0.1（2025-04-02）
更新文档
## 1.0.0（2025-04-02）
支持鸿蒙push
