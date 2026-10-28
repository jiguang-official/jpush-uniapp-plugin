# 本地契约测试

测试不进入DCloud模块上传目录。

- Harmony：在本目录安装开发依赖后运行`npm test`；执行真实app-harmony UTS源码的TypeScript转译结果，SDK/Ability由mock替代。此项不等于ArkTS/HAP编译。
- iOS codec：仓库根运行`swiftc uni_modules/jg-jpush-u/utssdk/app-ios/JPushUpgradeCodec.swift tests/ios/main.swift -o /tmp/jpush-codec-test`，执行该产物。覆盖hex/null、32bit序号、标识及并发失效，不证明iOS SDK真实回调。
- Android：使用HBuilderX生成本次模块的index.kt，与本模块SubscriptionResultCodec.java、正式JPush6.3.0/JCore5.6.0及HBuilder对应runtime一起编译；JUnit4/Robolectric4.14.1执行tests/android。Core AAR的libs内还含公开门面jar，必须一并加入classpath。测试使用真实生成Kotlin和Java，未复制实现。Android SDK28测试环境；测试classpath需要解包AndroidX test monitor/espresso的AAR classes.jar（普通JVM Gradle不会自动解包AAR）。

实际验证工具为HBuilderX5.15、Kotlin2.2.0、JDK17。完整APK与真机需匹配合法自定义基座/离线配置，测试不能替代该项。
