## BaseActivity
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
- more comes with extended Activities.

And other activities in ```devbox-components```, find what you need or just extend from BaseActivity