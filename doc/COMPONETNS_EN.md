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

### Once
A simple helper for check a block is called once in current state. It uses SharedPreference for key storage.
```java
// check a key has been used.
boolean isFirstBoot = Once.check("isFirstBoot");
if (isFirstBoot) {
    init();
}
```

if you like non-blocking style, then consider using callback style
```java
Once.check("isFirstBoot", new OnceCallback() {
    @Override public void onOnce() {
        init();
    }
});

```

you can also reset key state by using reset()
```java
Once.reset("isFirstBoot");
```