### DataLoader
DataLoader help you to control data load requests. See demo [ReposFragment](https://github.com/xxxifan/Devbox2/blob/master/app/src/main/java/com/xxxifan/devbox/demo/ui/view/main/ReposFragment.java)

The core idea is you don't need to care about when to load/reload your data, Dataloader will handle it for you.
```java

```
and some other methods is used by host activity/fragment, see BaseActivity/BaseFragment for more detail.

onLoadStart() will be called automatically in onResume(). In additional if you enabled lazyLoad in fragment, it will be called in onVisible() instead of onResume(), the best practice is using fragments with ViewPager, it will call setUserVisibleHint, which is onVisible() comes from.

Base already handle savedInstance for dataLoader.

In [RecyclerFragment](https://github.com/xxxifan/Devbox2/blob/master/library/src/main/java/com/xxxifan/devbox/library/base/extended/RecyclerFragment.java) it supply some methods to connect wit DataLoader
```java
enableScrollToLoad(lastItemNum);
notifyDataLoaded();
```
if scrollToLoad enabled, it will add a listener to RecyclerView and trigger dataLoader.startLoad() when scroll to bottom;
