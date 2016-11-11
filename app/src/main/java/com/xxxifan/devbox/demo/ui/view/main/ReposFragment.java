package com.xxxifan.devbox.demo.ui.view.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.demo.data.model.Repo;
import com.xxxifan.devbox.demo.repository.GithubService;
import com.xxxifan.devbox.core.base.BaseAdapterItem;
import com.xxxifan.devbox.core.base.DataLoader;
import com.xxxifan.devbox.components.RecyclerFragment;
import com.xxxifan.devbox.core.event.NetworkEvent;
import com.xxxifan.devbox.core.util.ViewUtils;
import com.xxxifan.devbox.components.http.Http;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kale.adapter.item.AdapterItem;
import rx.functions.Action1;

/**
 * Created by xifan on 6/17/16.
 */
public class ReposFragment extends RecyclerFragment<Repo> implements DataLoader.ListLoadCallback {

    private List<Repo> mRepoList = new ArrayList<>();

    @Subscribe
    public void onNetworkEvent(NetworkEvent event) {
        showMessage(event.message);
    }

    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {
        registerDataLoader(true, this).enableLazyLoad();
        registerEventBus();
        enableScrollToLoad(2);
        getRecyclerView().setClipToPadding(true);

        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText("暂无数据");
        setEmptyView(textView);
    }

    @Override protected AdapterItem<Repo> onCreateAdapterItem(Object type) {
        TestAdapter item = new TestAdapter((int) type);
        item.setOnItemClickListener(new BaseAdapterItem.ItemClickListener<Repo>() {
            @Override public void onItemClick(View v, Repo data, int index) {
                showMessage("hehe " + data.description);
            }
        });
        return item;
    }

    public Object getAdapterItemType(Repo repo) {
        return repo.fork ? 1 : 0;
    }

    @Override @SuppressWarnings("unchecked") public boolean onLoadStart() {
//        Http.createRetroService(GithubService.class)
//                .getUserRepos(GithubService.REPO_TYPE_OWNER, GithubService.REPO_SORT_UPDATED, GithubService.DIRECTION_DESC)
//                .enqueue(new Callback<List<Repo>>() {
//                    @Override
//                    public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
//                        mRepoList.clear();
//                        mRepoList.addAll(response.body());
//                        notifyDataLoaded();
//                        getDataLoader().setDataEnd(true); // mark data load end
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Repo>> call, Throwable t) {
//                        t.printStackTrace();
//                    }
//                });
        Http.createRetroService(GithubService.class)
                .getRxUserRepos(GithubService.REPO_TYPE_OWNER, GithubService.REPO_SORT_UPDATED, GithubService.DIRECTION_DESC)
                .compose(io())
                .compose(getDataLoader().rxNotifier())
                .compose(ViewUtils.rxDialog(getContext()))
                .subscribe(new Action1<Object>() {
                    @Override public void call(Object repos) {
                        mRepoList.clear();
                        mRepoList.addAll((List<Repo>) repos);
                        getCommonRcvAdapter().setData(mRepoList);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
        return false;
    }

    @Override
    public String getSimpleName() {
        return "ReposFragment";
    }

    @Override public void onRefreshStart() {
        Logger.d("onRefreshStart called");
    }

    public class TestAdapter extends BaseAdapterItem<Repo> {
        @BindView(R.id.repo_name)
        TextView titleText;
        @BindView(R.id.repo_rank)
        TextView rankText;

        private int typeInt;

        public TestAdapter(int typeInt) {
            this.typeInt = typeInt;
        }

        @Override public void setViews() {
            ButterKnife.bind(this, getView());
            titleText = ButterKnife.findById(getView(), R.id.repo_name);
            rankText = ButterKnife.findById(getView(), R.id.repo_rank);
            getView().setBackgroundColor(typeInt == 1 ? Color.RED : Color.WHITE);
        }

        @Override public int getLayoutResId() {
            return R.layout.item_repos;
        }

        @Override public void handleData(Repo data, int position) {
            super.handleData(data, position);
            titleText.setText(data.name);
            String rankStr = String.format("Fork:%s Star:%s", data.forks, data.stargazers_count);
            rankText.setText(rankStr);
        }
    }

}
