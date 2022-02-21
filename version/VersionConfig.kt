/**
 * 依赖库路径
 */
object VersionConfig {
    // multidex
    const val multidex = "androidx.multidex:multidex:2.0.1"
    // rxjava
    const val rxjava = "io.reactivex.rxjava2:rxjava:2.2.7"
    // rxandroid
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.0"
    // retrofitAdapter
    const val retrofitAdapter = "com.squareup.retrofit2:adapter-rxjava2:2.4.0"
    // rxrelay
    const val rxrelay = "com.jakewharton.rxrelay2:rxrelay:2.0.0"
    // fastJson
    const val fastJson = "com.alibaba:fastjson:1.2.48"
    // okhttp3
    const val okhttp3 = "com.squareup.okhttp3:okhttp:4.9.1"
    // okhttp3LogInterceptor
    const val okhttp3LogInterceptor = "com.squareup.okhttp3:logging-interceptor:3.11.0"
    /* Retrofit 网络Lib 版本需要保持一致*/
    // retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:2.6.0"
    // converterGson
    const val converterGson = "com.squareup.retrofit2:converter-gson:2.6.0"
    // gson
    const val gson = "com.google.code.gson:gson:2.8.6"
    /*  配置读取Lib */
    // androidproperties
    const val androidproperties = "com.github.fernandodev.androidproperties:androidproperties:1.0.0"
    /*  所有最新的 AndroidX 版本可以参考 https://developer.android.com/jetpack/androidx/versions?hl=zh-cn*/
    /*  noinspection GradleDependency v4、v7、v13包*/
    // appcompat
    const val appcompat = "androidx.appcompat:appcompat:1.0.2"
    /*  design包*/
    // supportDesign
    const val supportDesign = "com.google.android.material:material:1.0.0"
    // androidxcore
    const val androidxcore = "androidx.core:core:1.5.0-alpha05"
    // recyclerview
    const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
    // constraintLayout
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    /*  图片库glide*/
    // glideCompiler
    const val glideCompiler = "com.github.bumptech.glide:compiler:4.8.0"
    // glide
    const val glide = "com.github.bumptech.glide:glide:4.9.0"
    // arouterRegister
    const val arouterRegister = "com.alibaba:arouter-register:1.0.2"
    /*  组件化Arouter路由*/
    // arouter
    const val arouter = "com.alibaba:arouter-api:1.5.0"
    /*  在需要使用arouter的module中添加*/
    // arouterCompiler
    const val arouterCompiler = "com.alibaba:arouter-compiler:1.2.2"
    /* 屏幕适配*/
    // autosize
    const val autosize = "me.jessyan:autosize:1.1.2"
    /* viewmodel*/
    // lifecycleViewModel
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.1.0"
    // lifecycleExtensions
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.1.0"
    // objectboxProcessor
    const val objectboxProcessor = "io.objectbox:objectbox-processor:3.1.0"
    // objectbrowser
    const val objectbrowser = "io.objectbox:objectbox-android-objectbrowser:3.1.0"
    // objectbox
    const val objectbox = "io.objectbox:objectbox-android:3.1.0"
    // uetool
    const val uetool = "me.ele:uetool:1.2.9"
    // uetoolnoop
    const val uetoolnoop = "me.ele:uetool-no-op:1.2.9"
    // crashcanary
    const val crashcanary = "fairy.easy.crashcanary:crashcanary-androidx:1.1.1"
    // crashcanarynoop
    const val crashcanarynoop = "fairy.easy.crashcanary:crashcanary-no-op:1.1.1"
    // leakcanary
    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.7"
    // leakcanaryPlumber
    const val leakcanaryPlumber = "com.squareup.leakcanary:plumber-android:2.7"
    // smartRefreshLayout
    const val smartRefreshLayout = "com.scwang.smartrefresh:SmartRefreshLayout:1.1.0-andx-11"
    // magicIndicator
    const val magicIndicator = "com.github.hackware1993:MagicIndicator:1.5.0"
    // pinyin
    const val pinyin = "com.github.promeg:tinypinyin:2.0.3"
    // webSocket
    const val webSocket = "org.java-websocket:Java-WebSocket:1.5.2"
    // scaleImageView
    const val scaleImageView = "com.davemorrissey.labs:subsampling-scale-image-view:3.10.0"
    // shadowlayout
    const val shadowlayout = "com.github.dmytrodanylyk.shadow-layout:library:1.0.3"
    // jsbridge
    const val jsbridge = "com.github.lzyzsd:jsbridge:1.0.4"
    // bannerViewPager
    const val bannerViewPager = "com.github.zhpanvip:BannerViewPager:3.5.4"
    // alioss
    const val alioss = "com.aliyun.dpa:oss-android-sdk:2.9.4"
    // weChat
    const val weChat = "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+"
    // mmkv
    const val mmkv = "com.tencent:mmkv-static:1.2.7"
    // relinker
    const val relinker = "com.getkeepsafe.relinker:relinker:1.4.1"
    // javapoet
    const val javapoet = "com.squareup:javapoet:1.10.0"
    // autoService
    const val autoService = "com.google.auto.service:auto-service:1.0-rc7"
    // kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0"
    // kotlinxCoroutinesCore
    const val kotlinxCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0"
    // kotlinxCoroutinesAndroid
    const val kotlinxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0"
    // kotlinxCoroutinesAdapter
    const val kotlinxCoroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    /* 个推普通版sdk*/
    // getuiSdk
    const val getuiSdk = "com.getui:gtsdk:3.2.7.0"
    /* 个推核心组件*/
    // getuiCore
    const val getuiCore = "com.getui:gtc:3.1.7.0"
    /*个推google play版sdk*/
    // getuiForGooglePlaySdk
    const val getuiForGooglePlaySdk = "com.getui:sdk-for-google-play:4.3.9.0"
    /*google FCM离线推送*/
    // offlinePushForFbCore
    const val offlinePushForFbCore = "com.google.firebase:firebase-core:17.0.0"
    // offlinePushForFbMsg
    const val offlinePushForFbMsg = "com.google.firebase:firebase-messaging:20.0.0"
    /*华为离线推送*/
    // offlinePushForHw
    const val offlinePushForHw = "com.getui.opt:hwp:3.1.0"
    // offlinePushForHwCore
    const val offlinePushForHwCore = "com.huawei.hms:push:6.1.0.300"
    /*小米离线推送*/
    // offlinePushForXm
    const val offlinePushForXm = "com.getui.opt:xmp:3.1.0"
    /*Oppo离线推送*/
    // offlinePushForOppo
    const val offlinePushForOppo = "com.assist-v3:oppo:3.1.0"
    //vivo离线推送*/
    // offlinePushForVivo
    const val offlinePushForVivo = "com.assist-v3:vivo:3.1.0"
    // qq
    const val qq = "com.tencent.tauth:qqopensdk:3.52.0"
    /*二维码*/
    // zxing
    const val zxing = "com.google.zxing:core:3.3.0"
    /* 检查是否是模拟器*/
    // emulatorDetector
    const val emulatorDetector = "com.github.framgia:android-emulator-detector:1.4.1"
}
