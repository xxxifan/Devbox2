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

### Special lifecycle

onConfigActivity();<br/>
```// onCreate() Start```<br/>
getLayoutId();<br/>
setActivityView();<br/>
attachContentView();<br/>
inflateComponents();<br/>
onSetupActivity();<br/>
```// onCreate() End```<br/>

### Since 0.6.0
All functions in activities(e.g. TranslucentActivity, DrawerActivity...) has been refactor to ```UIComponent``` like [ToolbarComponent](https://github.com/xxxifan/Devbox2/blob/master/devbox-core/src/main/java/com/xxxifan/devbox/core/base/uicomponent/ToolbarComponent.java).
In that way users can write own components and much easier to control.
When you need to add your component to an activity, just do override ```attachContentView``` in a BaseActivity:

```java
@Override protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
    super.attachContentView(containerView, layoutResID); // do NOT remove this line
    addUIComponents(new ToolbarComponent());
}
```

What if you have a deep inherit activity and how to control your components? (like TranslucentDrawerActivity)
you can use ```getUIComponents()``` to get your UIComponents and operate it as ArrayMap.
you can also retrieve a UIComponent in a BaseActivity by its tag:
```java
ToolbarComponent toolbarComponent = getUIComponent(ToolbarComponent.TAG, ToolbarComponent.class);
```

### Other functions

- Lifecycle control with RxLifeCycle
- Eventbus, back key listener, rx schedulers control.
- More comes with ```devbox-components```, find what you need or just extend from BaseActivity