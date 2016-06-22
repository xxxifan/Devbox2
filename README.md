## Devbox2 ![travis](https://travis-ci.org/xxxifan/Devbox2.svg?branch=master)
Yet another Android development toolbox.
It's a new generation of [DevBox](https://github.com/xxxifan/DevBox)

## Usage
1. add this project as your module (maybe publish this to jitpack? but this won't allow you select what dependencies you need)
2. add apt needed dependencies to your main project.
3. init Devbox

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

Other functions:

- lifecycle control with RxLifeCycle
- Eventbus, back key listener, rx schedulers control.
- more comes with extends.

And other Activities in packages ```com.xxxifan.devbox.library.base.extended```, find what you need or just extend from BaseActivity

### BaseFragment
almost same functions with BaseActivity, it will be come more extends with fragment.
it also handled fragment visible state while restoreSavedInstance, so you just need to init fragments when savedInstance is null
```
    protected void onSetupActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragments.checkout(this, new TestFragment1())
                    .into(FRAGMENT_CONTAINER);
        }
    }
```

### Fragments
Here present you Fragments. This is a helper class that help you quickly and nicely deal with fragment transaction.
All operation is chained, you can follow this example to take a quick look:

```
// checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is R.id.fragment_container
// it will use BaseFragment.getSimpleName() as tag, or SimpleClassName if fallback.
Fragments.checkout(this, new TestFragment())
        .into(FRAGMENT_CONTAINER);

// checkout with specified tag
Fragments.checkout(this, new TestFragment(), "test")
        .into(FRAGMENT_CONTAINER);

// more options
Fragments.checkout(this, new TestFragment(), "test")
        .addToBackStack(true) // it will use tag name as state name
        .replaceLast(true) // replace last fragment, default is true.
        .removeLast(true) // remove last fragment while checkout.
        .addSharedElement(view, name) // not tested
        .setCustomAnimator(enter, exit) // not tested
        .fade()
        .into(FRAGMENT_CONTAINER);

// add multi fragments
Fragments.add(this, new Fragment1(), new Fragment2(), new Fragment3())
        .into(R.id.container1, R.id.container2, R.id.container3);

```

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

~~And there's a special class named UiController which contains view reference and lifecycle controlled by BaseActivity, for leaner view control.If you want to use it, you need to register it at where it begins.that's all you need to do, the rest have already handled by BaseActivity.~~

```UiController now is deprecated from this library```


### AppPref
A wrapper for SharedPreference, you can simply use putXxx/getXxx to save variables, and also get chained editor by edit() just like what SharedPreference do.

```
AppPref.putString(key, value);
AppPref.edit()
        .putString(key, value)
        .putInt(key,101)
        .apply();
```

Also, you can get SharedPreference instance by specify preference name

```
// AppPref.getPrefs();
AppPref.getPrefs(prefName);
```

### Once
A simple helper for check a block is called once in current state. It uses SharedPreference for key storage.
```
// check a key has been used.
boolean isFirstBoot = Once.check("isFirstBoot");
if (isFirstBoot) {
    init();
}
```

if you like non-blocking style, then consider using callback style
```
Once.check("isFirstBoot", new OnceCallback() {
    @Override public void onOnce() {
        init();
    }
});

```

you can also reset key state by using reset()
```
Once.reset("isFirstBoot");
```


## TODO
- [x] Network part (Including image load, file download)
- [ ] Fragment loader
- [ ] More extends for fragments (like recyclerView support)
- [ ] Multidex
- [ ] More to come

## Dependencies

    compile "com.android.support:appcompat-v7:${support_lib}"

    /*
     * Network/Loaders
     */
    compile "com.squareup.okhttp3:okhttp:${okhttp}"
    compile "com.squareup.okhttp3:logging-interceptor:${okhttp}"
    compile "com.squareup.retrofit2:retrofit:${retrofit}"
    compile "com.squareup.retrofit2:adapter-rxjava:${retrofit}"
    compile "com.squareup.retrofit2:converter-gson:${retrofit}"
    compile 'com.github.bumptech.glide:glide:3.7.0'
    compile 'com.github.bumptech.glide:okhttp3-integration:1.4.0@aar'
    compile 'com.liulishuo.filedownloader:library:0.3.2'

    /*
     * Rx
     */
    compile 'io.reactivex:rxjava:1.1.6'
    compile 'io.reactivex:rxandroid:1.2.1'
    compile 'com.trello:rxlifecycle:0.6.1'
    compile 'com.tbruyelle.rxpermissions:rxpermissions:0.7.0@aar'
    compile "com.github.VictorAlbertos:RxActivityResult:0.3.4"

    /*
     * Dagger
     */
    compile 'com.google.dagger:dagger:2.2'

    /*
     * Tools
     */
    compile 'com.jakewharton:butterknife:8.1.0'
    compile 'com.afollestad.material-dialogs:core:0.8.5.9'
    compile 'org.greenrobot:eventbus:3.0.0'
    compile 'com.google.code.gson:gson:2.7'
    compile 'com.github.tianzhijiexian:CommonAdapter:1.1.9'

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
     * apt 'com.jakewharton:butterknife-compiler:8.1.0'
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
