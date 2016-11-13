### Fragments
Fragments是一个帮助你快速优雅的处理fragment transaction。
所有操作都可以被链起来，你可以通过下面的例子来快速浏览一下。

```java
Fragments.checkout(this, new TestFragment())
        .into(FRAGMENT_CONTAINER);
```
切换Fragment到FRAGEMNT_CONTAINER这个布局中去，这是一个在BaseActivity中内置的值，也就是  R.id.fragment_container .
在这执行后，会将fragment的getSimpleName作为tag，如果这不是一个BaseFragment，那将用Class的getSimpleName()。

指定一个tag
```
Fragments.checkout(this, new TestFragment(), "test")
        .into(FRAGMENT_CONTAINER);
```

其他选项
```
Fragments.checkout(this, new TestFragment(), "test")
        .data(bundle) // set bundle arguments
        .addToBackStack(true) // it will use tag name as state name
        .hideLast(true) // replace last fragment, default is true.
        .removeLast(true) // remove last fragment while checkout.
        .addSharedElement(view, name)
        .setCustomAnimator(enter, exit)
        .fade() // default fade animation
        .into(FRAGMENT_CONTAINER);
```

同时切换多个
```
Fragments.add(this, new Fragment1(), new Fragment2(), new Fragment3())
        .into(R.id.container1, R.id.container2, R.id.container3);
```
