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

其他功能支持:

- 集成RxLifeCycle的生命周期管理
- Eventbus, 返回事件监听, rx 线程调度器控制.
- 更多方便的继承自BaseActivity的类在components模块中.