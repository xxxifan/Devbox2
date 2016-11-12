### Fragments
Here present you Fragments. This is a helper class that help you quickly and nicely deal with fragment transaction.
All operations are chained, you can follow this example to take a quick look:

```java
// checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is R.id.fragment_container
// it will use BaseFragment.getSimpleName() as tag, or SimpleClassName if fallback.
Fragments.checkout(this, new TestFragment())
        .into(FRAGMENT_CONTAINER);

// checkout with specified tag
Fragments.checkout(this, new TestFragment(), "test")
        .into(FRAGMENT_CONTAINER);

// more options
Fragments.checkout(this, new TestFragment(), "test")
        .data(bundle) // set bundle arguments
        .addToBackStack(true) // it will use tag name as state name
        .hideLast(true) // replace last fragment, default is true.
        .removeLast(true) // remove last fragment while checkout.
        .addSharedElement(view, name)
        .setCustomAnimator(enter, exit)
        .fade() // default fade animation
        .into(FRAGMENT_CONTAINER);

// add multi fragments
Fragments.add(this, new Fragment1(), new Fragment2(), new Fragment3())
        .into(R.id.container1, R.id.container2, R.id.container3);
```
