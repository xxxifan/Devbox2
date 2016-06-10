## Devbox2 ![travis](https://travis-ci.org/xxxifan/Devbox2.svg?branch=master)
Yet another personal Android development tool Set.
It's a new generation of [DevBox](https://github.com/xxxifan/DevBox)

## Usage
1. add this project as your module (maybe publish this to jitpack? but this won't allow you select what dependencies you need)
2. add apt needed dependencies to your main project.
3. init Devbox

```java
Devbox.init(applicationContext);
```

```warning: using this library may add up your method count at least 15000+.```

## Classes

### BaseActivity
It used a special way to load views, but it would be simpler to implement a Activity, and there's more functional activities for extends.
A Simple Activity looks like this:

```java
public class MainActivity extends TranslucentDrawerActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onConfigActivity() {
        super.onConfigActivity();
        transparentStatusBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {
        // Do things like view init and data load start here.
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        transparentToolbar();
    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.activity_main, null);
    }
}

```

And other Activities in packages ```com.xxxifan.devbox.library.base.extended```, find what you need or just extend from BaseActivity

### MVP Model
I have wrote two interface for mvp model, all you need to do is create a contract and have two interface inside extends from BasePresenter and BaseView.

```java
public interface MvpContract {
    interface View extends BaseView<Presenter> {
        void onUiChanged();
    }

    interface Presenter extends BasePresenter {
        void doThings();
    }
}
```

And there's a special class named UiController which contains view reference and lifecycle controlled by BaseActivity, for leaner view control.
If you want to use it, you need to register it at where it begins.

```java
public class MainActivity extends ToolbarActivity {

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {
        registerUiControllers(new BottomBarControler(view));
    }
    ......
}
```

that's all you need to do, the rest have already handled by BaseActivity.

### AppPref
A wrapper for SharedPreference, you can simply use putXxx/getXxx to save variables, and also get chained editor by edit() just like what SharedPreference do.

```java
AppPref.putString(key, value);
AppPref.edit()
        .putString(key, value)
        .putInt(key,101)
        .apply();
```

Also, you can get SharedPreference instance by specify preference name

```java
// AppPref.getPrefs();
AppPref.getPrefs(prefName);
```

More to come.

## Dependencies

    compile "com.android.support:appcompat-v7:${support_lib}"

    /*
     * Network/Loaders
     */
    compile "com.squareup.okhttp3:okhttp:${okhttp}"
    compile 'com.squareup.retrofit2:retrofit:2.0.2'
    compile 'com.github.bumptech.glide:glide:3.7.0'
    compile 'com.github.bumptech.glide:okhttp3-integration:1.4.0@aar'
    compile 'com.liulishuo.filedownloader:library:0.3.1'

    /*
     * Rx
     */
    compile 'io.reactivex:rxjava:1.1.5'
    compile 'io.reactivex:rxandroid:1.2.0'
    compile 'com.trello:rxlifecycle:0.6.1'
    compile 'com.tbruyelle.rxpermissions:rxpermissions:0.7.0@aar'
    compile "com.github.VictorAlbertos:RxActivityResult:0.3.3"

    /*
     * Dagger
     */
    compile 'com.google.dagger:dagger:2.2'

    /*
     * Tools
     */
    compile 'com.jakewharton:butterknife:8.0.1'
    compile 'com.afollestad.material-dialogs:core:0.8.5.9'
    compile 'org.greenrobot:eventbus:3.0.0'
    compile 'com.google.code.gson:gson:2.6.2'

    /*
     * Leakcanary
     */
    debugCompile "com.squareup.leakcanary:leakcanary-android:${leakcanary}"
    releaseCompile "com.squareup.leakcanary:leakcanary-android-no-op:${leakcanary}"
    testCompile "com.squareup.leakcanary:leakcanary-android-no-op:${leakcanary}"

    /*
     * Test
     */
    testCompile 'junit:junit:4.12'
    testCompile "com.squareup.okhttp3:mockwebserver:${okhttp}"
    testCompile 'org.mockito:mockito-core:1.10.19'

    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'
    androidTestCompile 'com.android.support.test:runner:0.5'
    androidTestCompile "com.android.support:support-annotations:${support_lib}"

    provided 'com.android.support:multidex:1.0.1'

    /*
     * Optional
     */
    compile 'jp.wasabeef:glide-transformations:2.0.1'
    compile 'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.3.0'

    /*
     * apt needed
     *
     * apply plugin: 'com.neenbedankt.android-apt'
     * apt 'com.google.dagger:dagger-compiler:2.2'
     * apt 'com.jakewharton:butterknife-compiler:8.0.1'
     */

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