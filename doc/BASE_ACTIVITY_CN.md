## BaseActivity
在这个类中，使用了比较复杂的方法去加载布局，但是能更加方便的去组合布局效果。
在继承了该Activity之后将拥有这些基本的方法可以覆写：

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
其中只有 ```getLayoutId```, ```onSetupActivity```, ```getSimpleName``` 是必须覆写的

### 0.6.0 更新
所有集成在 Activity 的模块(比如 TranslucentActivity, DrawerActivity 里面的功能)已经被重构成了 ```UIComponent``` 例如 [ToolbarComponent](https://github.com/xxxifan/Devbox2/blob/master/devbox-core/src/main/java/com/xxxifan/devbox/core/base/uicomponent/ToolbarComponent.java)。
这样能够方便用户编写自己的模块，更加方便灵活。
当添加一个模块到 Activity 的时候，只需要这样做覆写 attachContentView 方法：

```java
@Override protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
    super.attachContentView(containerView, layoutResID); // 不能移除该行
    addUIComponents(new ToolbarComponent());
}
```

如果你有一个继承了多次的 Activity 应该怎样去管理 UIComponent呢？
你可以使用 ```getUIComponents()``` 来获取到你的 UIComponent 并像 ArrayMap 那样进行操作。
你也可以直接通过 tag 获取一个 UIComponent 在 BaseActivity 里面：
```java
ToolbarComponent toolbarComponent = getUIComponent(ToolbarComponent.TAG, ToolbarComponent.class);
```

### 其他功能

- 集成RxLifeCycle的生命周期管理
- Eventbus, 返回事件监听, rx 线程调度器控制.
- 更多方便的继承自BaseActivity的类在components模块中.