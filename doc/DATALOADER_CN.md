### DataLoader
DataLoader 能帮助你控制数据加载等请求，可以查看[例子](https://github.com/xxxifan/Devbox2/blob/master/app/src/main/java/com/xxxifan/devbox/demo/ui/view/main/ReposFragment.java)

设计的理念是帮助你不用考虑什么时候应该开始加载数据，DataLoader会处理这些情况<br/>

首先，需要绑定callback
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

然后在回调中加载你的数据
```java
    @Override public boolean onLoadStart() {
        // do load things
    }

    // 当使用ListCallback时才会需要实现这个方法
    @Override public boolean onRefreshStart() {
        // do load things
    }
```
```onLoadStart``` 会被 ```onResume()``` 以及开启 ```LazyLoad``` 时被 ```onVisible()``` 触发
它们都会同时只执行一个任务以防止重复的数据加载。（多次的请求会被丢弃）

现在所有的都已配置完毕，但是你需要告诉DataLoader什么时候数据加载完成或失败，这样才不会始终重复的去加载数据
```java
    dataLoader.notifyPageLoaded();
    dataLoader.notifyPageLoadFailed();
```
当你使用RxJava的时候，这个操作能够用一行代码完成并且不需要去考虑什么时候去执行，你只需要在RxJava中加入这一句
```java
    rxRequest()
        .compose(getDataLoader().rxNotifier())
        .subscribe();
```

除此之外，你可以手动触发数据加载
```java
    dataLoader.startLoad();
    dataLoader.startLazyLoad();
    dataLoader.startRefresh();
    dataLoader.forceLoad(); // functions above also check current load state and network, use this to force load
```

每次加载都会为page这个值加一，所以你可以从```getPage()```方法获取到正确的页码，同时不要忘记在刷新之后调用```resetPage()```重置页码<br/>
page值从1开始计算。

还有一些方法与Base类集成了起来，具体请查看BaseActivity/BaseFragment

在 [RecyclerFragment](https://github.com/xxxifan/Devbox2/blob/master/library/src/main/java/com/xxxifan/devbox/library/base/extended/RecyclerFragment.java)也提供了一些方法与DataLoader关联了起来
如果启用了```enableScrollToLoad```，将添加一个RecyclerView的监听器，当滑动到底部时会触发```startLoad()```方法
