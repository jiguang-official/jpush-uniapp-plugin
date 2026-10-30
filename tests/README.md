# 开发检查

本目录为公开的开发检查用例，不属于DCloud模块上传目录。

- Harmony：安装本目录开发依赖后运行`npm test`。用例通过TypeScript执行UTS逻辑，并替代SDK/Ability边界；不替代ArkTS/HAP编译。
- iOS codec：在仓库根执行`swiftc uni_modules/jg-jpush-u/utssdk/app-ios/JPushUpgradeCodec.swift tests/ios/main.swift -o /tmp/jpush-codec-test`，再运行生成程序。用于检查参数转换和回调隔离，不替代iOS SDK真实回调。
- Android：使用支持UTS混编的HBuilderX生成模块Kotlin代码，与SubscriptionResultCodec.java、对应JPush/JCore及DCloud runtime编译后执行`tests/android`中的JUnit/Robolectric用例。JCore AAR内的公开门面jar需加入classpath；普通JVM Gradle环境还需解包AndroidX test monitor/espresso的AAR classes.jar。

集成应用仍需使用匹配的合法自定义基座或离线配置，并验证对应设备、权限及推送通道。
