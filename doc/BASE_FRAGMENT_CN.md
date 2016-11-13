### BaseFragment
与BaseActivity基本一致，**但是它已经处理过了savedInstance来控制fragment的显示状态，所以你只需要在savedInstance为空的时候初始化你的fragment**
```java
protected void onSetupActivity(Bundle savedInstanceState) {
    if (savedInstanceState == null) {
        Fragments.checkout(this, new TestFragment1())
                .into(FRAGMENT_CONTAINER);
    }
}
```
可能你会问那这样Fragment如何去管理，通过内置的```Fragments```能够很方便的处理这些情况