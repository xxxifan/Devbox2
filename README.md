[EN|[简体中文](https://github.com/xxxifan/Devbox2/tree/master/doc/README_CN.md)]
## Devbox2 [![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=578cb3974a7be5010070ef32&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/578cb3974a7be5010070ef32/build/latest) [![Jitpack.io](https://jitpack.io/v/xxxifan/Devbox2.svg)](https://jitpack.io/#xxxifan/Devbox2)
Yet another Android development toolbox.
It's a new generation of [DevBox](https://github.com/xxxifan/DevBox)<br/>

## Features

*   *Easier UI control*
*   *Fast&Beautiful Fragment control*
*   *Useful Utils inside*

## Usage
1.add this project as your module ~~(maybe publish this to jitpack? but this won't allow you select what dependencies you need)~~
or config it in gradle way now!

```groovy
// in root build.gradle file
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}

// in app build.gradle file
def devbox_version = "v0.5.0"
dependencies {
        // ...
        compile "com.github.xxxifan.Devbox2:devbox-core:${devbox_version}"
        // components
        compile "com.github.xxxifan.Devbox2:devbox-components:${devbox_version}"
}
```

check [LatestRelease](https://github.com/xxxifan/Devbox2/releases) here

2.add apt needed dependencies to your main project.

3.init Devbox

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

4.use Devbox.Theme or turn off your windowActionBar in your style

```
<style name="AppTheme" parent="Devbox.AppTheme">
</style>
```

5.feel free to copy a proguard rules to your app project from here
> [proguard-rules.pro](https://github.com/xxxifan/Devbox2/blob/master/library/proguard-rules.pro)

6.if you using ```devbox-componetns```, Butterknife and some library need android-apt, and also, if you want to enable retrolambda, add following in you root build.gradle

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
and in your app build.gradle

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

7.Required Permissions
You may need basic network permissions
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

~~warning: using this library may add up your method count at least 15000+~~<br/>
***Warning: using core libray may add up your method count at least 8500+***<br/>
***Warning: using core + components library may add up your method count at least 16000+***

## Changelog

see [release tag](https://github.com/xxxifan/Devbox2/releases)

## Core<br/>

>[BaseActivity](https://github.com/xxxifan/Devbox2/tree/master/doc/BASE_ACTIVITY_EN.md)<br/>
>[BaseFragment](https://github.com/xxxifan/Devbox2/tree/master/doc/BASE_FRAGMENT_EN.md)<br/>
>[Fragments](https://github.com/xxxifan/Devbox2/tree/master/doc/FRAGMENTS_EN.md)<br/>
>[DataLoader](https://github.com/xxxifan/Devbox2/tree/master/doc/DATALOADER_EN.md)<br/>

## Utils

>AppPref        - easier to access SharedPreference<br/>
>Once           - quick to check whether to run one time code<br/>
>Strings        - util collection for string<br/>
>FieldChecker   - easier to check a entity fields is required<br/>
>IOUtils, ViewUtils, ReflectUtils, StatisticalUtil - like the name does<br/>
>[Read introductions](https://github.com/xxxifan/Devbox2/tree/master/doc/COMPONENTS_EN.md)

## Components
from 0.5.0 devbox splited into two module, core and components, so feel free to choose what you need. Components package including:
>DrawerActivity<br/>
>TranslucentActivity<br/>
>TranslucentDrawerActivity<br/>
>ImageTranslucentActivity<br/>
>RecyclerFragment<br/>
>Http util, support normal requests, upload, download<br/>
>okhttp, retrofit, glide, butterknife, gson and some other useful libraries.<br/>
>[Read Source](https://github.com/xxxifan/Devbox2/tree/master/devbox-components)

## TODO
- [x] Network part (Including image load, file download)
- [x] DataLoader
- [x] More extends for fragments (like recyclerView support)
- [x] IOUtils based on okio
- [ ] NotificationUtils
- [ ] More

## Dependencies

    testCompile 'junit:junit:4.12'
    
    compile "com.android.support:design:${support_lib}"
    /*
     * Network/Loaders
     */
    compile "com.squareup.okhttp3:okhttp:${okhttp}"
    compile "com.squareup.okhttp3:logging-interceptor:${okhttp}"
    compile 'com.github.franmontiel:PersistentCookieJar:v1.0.0'
    compile "com.squareup.retrofit2:retrofit:${retrofit}"
    compile "com.squareup.retrofit2:adapter-rxjava:${retrofit}"
    compile "com.squareup.retrofit2:converter-gson:${retrofit}"
    compile 'com.github.bumptech.glide:glide:3.7.0'
    compile 'com.github.bumptech.glide:okhttp3-integration:1.4.0@aar'
    compile 'com.liulishuo.filedownloader:library:1.0.2'

    /*
     * Rx
     */
    compile 'io.reactivex:rxjava:1.2.1'
    compile 'io.reactivex:rxandroid:1.2.1'
    compile 'com.trello:rxlifecycle:0.8.0'
    compile 'com.trello:rxlifecycle-android:0.8.0'
    compile 'com.jakewharton.rxrelay:rxrelay:1.1.0'
    compile 'com.tbruyelle.rxpermissions:rxpermissions:0.8.0@aar'
    compile "com.github.VictorAlbertos:RxActivityResult:0.3.7"

    /*
     * Tools
     */
    compile 'com.jakewharton:butterknife:8.4.0'
    compile 'com.afollestad.material-dialogs:core:0.8.6.2' // not update until cqqk stable release.
    compile 'org.greenrobot:eventbus:3.0.0'
    compile 'com.google.code.gson:gson:2.7'
    compile 'com.github.tianzhijiexian:CommonAdapter:1.2.0'
    compile 'com.orhanobut:logger:1.15'
    compile 'top.zibin:Luban:1.0.7'

    /*
     * Optional
     */
    compile 'jp.wasabeef:glide-transformations:2.0.1'
    compile 'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.3.0'
    compile 'com.amitshekhar.android:glide-bitmap-pool:0.0.1'

    /*
     * apt needed
     *
     * apply plugin: 'com.neenbedankt.android-apt'
     * apt 'com.jakewharton:butterknife-compiler:8.4.0'
     */

Java files included in library:

[SystemBarTintManager](https://github.com/xxxifan/Devbox2/blob/master/library/src/main/java/com/xxxifan/devbox/library/base/SystemBarTintManager.java)

[PinchImageView](https://github.com/xxxifan/Devbox2/blob/master/library/src/main/java/com/xxxifan/devbox/library/widget/PinchImageView.java)


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
