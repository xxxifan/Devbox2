[[EN](https://github.com/xxxifan/Devbox2/blob/master/README.md)|简体中文]
## Devbox2 [![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=578cb3974a7be5010070ef32&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/578cb3974a7be5010070ef32/build/latest) [![Jitpack.io](https://jitpack.io/v/xxxifan/Devbox2.svg)](https://jitpack.io/#xxxifan/Devbox2)

## 使用方法
1.添加Gradle依赖
```groovy
// in root build.gradle file
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}

// in app build.gradle file
def devbox_version = "0.5.0"
dependencies {
        // ...
        compile "com.github.xxxifan.Devbox2:devbox-core:${devbox_version}"
        // components
        compile "com.github.xxxifan.Devbox2:devbox-components:${devbox_version}"
}
```

查看[最新版本号](https://github.com/xxxifan/Devbox2/releases)

2.在Application中初始化

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init devbox
        Devbox.init(this);

        // do other init
    }
}
```

3.在 style 文件中使用 ```Devbox.Theme``` 或禁用 ```windowActionBar```

```
<style name="AppTheme" parent="Devbox.AppTheme">
</style>
```

4.有必要时添加混淆规则到你的app文件夹下
> [proguard-rules.pro](https://github.com/xxxifan/Devbox2/blob/master/library/proguard-rules.pro)

5.如果使用了 ```devbox-componetns``` 模块, Butterknife 和一些三方库需要 android-apt, 以及 retrolambda 支持，需要添加以下代码到你的根目录下的 build.gradle 文件

```groovy
buildscript {
    // ...
    dependencies {
        // ...
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8' // android-apt
        classpath 'me.tatarka:gradle-retrolambda:3.3.1' // retrolamda
    }

    // don't forget these
    ext {
        minSdk = 16 // min sdk is 15, 16 is recommended
        sdk = 24 // target sdk version
        buildTool = "24.0.3" // your build tool version here

        // dependencies
        support_lib = "24.2.1" // add this line
        okhttp = "3.4.2" // add this line if you're using devbox-componets
        retrofit = "2.1.0" // add this line if you're using devbox-componets
    }
}
```
and your app build.gradle

```groovy
apply plugin: 'me.tatarka.retrolambda'
apply plugin: 'com.neenbedankt.android-apt'

android {
    compileSdkVersion sdk as int
    buildToolsVersion buildTool as String
    defaultConfig {
        // recommended to use this universal version
        minSdkVersion minSdk as int
        targetSdkVersion sdk as int
    }
    // ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    // ...
    dependencies {
        // optional
        apt 'com.jakewharton:butterknife-compiler:8.4.0'
    }
}
```

7.需要设置以下权限<br/>
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

***注意: 使用 core 会增加至少 8500 的方法数***<br/>
***注意: 同时使用 core 和 components 会增加至少 16000 的方法数***

## 更新日志

查看[release标签](https://github.com/xxxifan/Devbox2/releases)

## 指南
### Core<br/>
>[BaseActivity](https://github.com/xxxifan/Devbox2/tree/master/doc/BASE_ACTIVITY_CN.md)<br/>
>[BaseFragment](https://github.com/xxxifan/Devbox2/tree/master/doc/BASE_FRAGMENT_CN.md)<br/>
>[Fragments](https://github.com/xxxifan/Devbox2/tree/master/doc/FRAGMENTS_CN.md)<br/>
>[DataLoader](https://github.com/xxxifan/Devbox2/tree/master/doc/DATALOADER_CN.md)<br/>

### Components
从 0.5.0 开始 Devbox 拆分成了 core 和 components 两个模块，添加你所需要的模块即可，components包含了以下功能
>DrawerActivity<br/>
>TranslucentActivity<br/>
>TranslucentDrawerActivity<br/>
>ImageTranslucentActivity<br/>
>RecyclerFragment<br/>
>Http util, 支持普通的请求，上传下载等<br/>
>okhttp, retrofit, glide, butterknife, gson 以及其他一些第三方库.<br/>
>[查看源码](https://github.com/xxxifan/Devbox2/tree/master/devbox-components)

### 工具类
>AppPref        - 更方便的使用 SharedPreference<br/>
>Once           - 快捷的检查只执行一次的代码块<br/>
>Strings        - String工具集<br/>
>FieldChecker   - 方便的检查一个实体类的属性值是否为空<br/>
>IOUtils, ViewUtils, ReflectUtils, StatisticalUtil - 如题<br/>
>[查看介绍](https://github.com/xxxifan/Devbox2/tree/master/doc/COMPONENTS_CN.md)

## License
```
Copyright 2016 xxxifan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
