package com.xxxifan.devbox.demo.ui.view.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.demo.data.model.Repo;
import com.xxxifan.devbox.demo.repository.GithubService;
import com.xxxifan.devbox.library.base.BaseAdapterItem;
import com.xxxifan.devbox.library.base.DataLoader;
import com.xxxifan.devbox.library.base.extended.RecyclerFragment;
import com.xxxifan.devbox.library.event.NetworkEvent;
import com.xxxifan.devbox.library.util.ViewUtils;
import com.xxxifan.devbox.library.util.http.Http;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kale.adapter.CommonRcvAdapter;
import kale.adapter.item.AdapterItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xifan on 6/17/16.
 */
public class ReposFragment extends RecyclerFragment implements DataLoader.ListLoadCallback {

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
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText("暂无数据");
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setEmptyView(textView);
    }

    @Override
    protected RecyclerView.Adapter setupAdapter() {
        return new CommonRcvAdapter<Repo>(mRepoList) {
            @NonNull
            @Override
            public AdapterItem<Repo> createItem(Object type) {
                final int typeInt = (int) type;
                return new BaseAdapterItem<Repo>() {
                    @BindView(R.id.repo_name)
                    TextView titleText;
                    @BindView(R.id.repo_rank)
                    TextView rankText;

                    @Override
                    protected void bindViews() {
                        ButterKnife.bind(this, getView());
                        titleText = ButterKnife.findById(getView(), R.id.repo_name);
                        rankText = ButterKnife.findById(getView(), R.id.repo_rank);
                        getView().setBackgroundColor(typeInt == 1 ? Color.RED : Color.WHITE);
                    }

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_repos;
                    }

                    @Override
                    public void handleData(Repo repo, int index) {
                        titleText.setText(repo.name);
                        String rankStr = String.format("Fork:%s Star:%s", repo.forks, repo.stargazers_count);
                        rankText.setText(rankStr);
                    }
                };
            }

            @Override public Object getItemType(Repo repo) {
                return repo.fork ? 1 : 0;
            }
        };
    }

    @Override public boolean onLoadStart() {
        final MaterialDialog dialog = ViewUtils.getLoadingDialog(getContext());
        dialog.show();
        Http.createRetroService(GithubService.class)
                .getUserRepos(GithubService.REPO_TYPE_OWNER, GithubService.REPO_SORT_UPDATED, GithubService.DIRECTION_DESC)
                .enqueue(new Callback<List<Repo>>() {
                    @Override
                    public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                        mRepoList.clear();
                        mRepoList.addAll(response.body());
                        notifyDataLoaded();
                        getDataLoader().setDataEnd(true); // mark data load end
                        ViewUtils.dismissDialog(dialog);
                    }

                    @Override
                    public void onFailure(Call<List<Repo>> call, Throwable t) {
                        t.printStackTrace();
                        ViewUtils.dismissDialog(dialog);
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
}
