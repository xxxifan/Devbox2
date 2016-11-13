### DataLoader
DataLoader help you to control data load requests. See demo [ReposFragment](https://github.com/xxxifan/Devbox2/blob/master/app/src/main/java/com/xxxifan/devbox/demo/ui/view/main/ReposFragment.java)

The core idea is you don't need to care about when to load/reload your data, Dataloader will handle it for you.<br/>
First, register dataloader
```java
    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {
        registerDataLoader(
                true, // true to ensure currently has network
                callback // DataLoader.LoadCallback or DataLoader.ListLoadCallback
        );
        enableScrollToLoad(2);
    }
```

Then do things in callback method
```java
    @Override public boolean onLoadStart() {
        // do load things
    }

    // you'll need it only when you use ListLoadCallback
    @Override public boolean onRefreshStart() {
        // do load things
    }
```
```onLoadStart``` will triggered by ```onResume()```, and ```onVisible()``` if ```LazyLoad``` enabled in BaseFragment
both of them will runs one task a time to avoid duplicate data load.

now things is setup, but you need to tell Dataloader when it's complete or failed, it will not auto load data again.
```java
    dataLoader.notifyPageLoaded();
    dataLoader.notifyPageLoadFailed();
```

if you're using RxJava, it can be merged into one line and don't need you to care about when to call it. All you need to do is handle onNext and onError correctly
```java
    rxRequest()
        .compose(getDataLoader().rxNotifier())
        .subscribe();
```

and you can trigger load/refresh manually
```java
    dataLoader.startLoad();
    dataLoader.startLazyLoad();
    dataLoader.startRefresh();
    dataLoader.forceLoad(); // functions above also check current load state and network, use this to force load
```

every load will cause page++, so you can get correct page number from ```getPage```, and don't forget ```resetPage``` after refresh
and getPage is started from 1.

some other methods is intergrated with Base, see BaseActivity/BaseFragment for more detail.

In [RecyclerFragment](https://github.com/xxxifan/Devbox2/blob/master/library/src/main/java/com/xxxifan/devbox/library/base/extended/RecyclerFragment.java) it supply some methods to connect wit DataLoader
if scrollToLoad enabled, it will add a listener to RecyclerView and trigger dataLoader.startLoad() when scroll to bottom;
