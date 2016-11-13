## Components
### MVP Model
我定制了两个mvp模式的接口，使用方法如下
```java
public interface MvpContract {
    interface View extends BaseView<Presenter> {
        void onUiChanged();
    }

    interface Presenter extends BasePresenter<View> {
        void doThings();
    }
}
```

### AppPref
封装了 SharedPreference, 你可以简单快速的使用 putXxx/getXxx 来保存值

```java
AppPref.putString(key, value);
AppPref.edit()
        .putString(key, value)
        .putInt(key,101)
        .apply();
```

同时也可以很方便的获取指定名称SharedPreference对象

```java
// AppPref.getPrefs();
AppPref.getPrefs(prefName);
```

### Once
一个简单的工具来检查一个代码块在app中是否被执行过一次，它使用了SharedPreference来记录
```java
// check a key has been used.
boolean isFirstBoot = Once.check("isFirstBoot");
if (isFirstBoot) {
    init();
}
```

如果你喜欢回调的方式也可以
```java
Once.check("isFirstBoot", new OnceCallback() {
    @Override public void onOnce() {
        init();
    }
});

```
也可以重置一个key
```java
Once.reset("isFirstBoot");
```

### FieldChecker
检查一个实体类的非静态对象是否为空，适合在提交表单时验证
```java
FieldChecker.checkNull(apple);
```