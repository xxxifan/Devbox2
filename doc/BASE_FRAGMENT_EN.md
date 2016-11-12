### BaseFragment
almost same functions with BaseActivity
it also **handled fragment visible state while restoreSavedInstance, so you just need to init fragments when savedInstance is null**
```java
protected void onSetupActivity(Bundle savedInstanceState) {
    if (savedInstanceState == null) {
        Fragments.checkout(this, new TestFragment1())
                .into(FRAGMENT_CONTAINER);
    }
}
```