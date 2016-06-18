package com.xxxifan.devbox.demo.ui.view.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.demo.data.model.Repo;
import com.xxxifan.devbox.demo.repository.GithubService;
import com.xxxifan.devbox.library.base.BaseAdapterItem;
import com.xxxifan.devbox.library.base.extended.RecyclerFragment;
import com.xxxifan.devbox.library.util.ViewUtils;
import com.xxxifan.devbox.library.util.http.Http;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kale.adapter.CommonRcvAdapter;
import kale.adapter.item.AdapterItem;
import rx.functions.Action1;

/**
 * Created by xifan on 6/17/16.
 */
public class ReposFragment extends RecyclerFragment {

    private List<Repo> mRepoList;

    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {
        mRepoList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Http.createRetroService(GithubService.class)
                .getUserRepos(GithubService.REPO_TYPE_OWNER, GithubService.REPO_SORT_UPDATED, GithubService.DIRECTION_DESC)
                .compose(io())
                .compose(ViewUtils.loadingObservable(getContext()))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        System.out.println(o);
                    }
                });
    }

    @Override
    protected CommonRcvAdapter setAdapter() {
        return new CommonRcvAdapter<Repo>(mRepoList) {
            @NonNull
            @Override
            public AdapterItem<Repo> createItem(Object type) {
                return new BaseAdapterItem<Repo>() {
                    @BindView(R.id.repo_name)
                    TextView titleText;
                    @BindView(R.id.repo_rank)
                    TextView rankText;

                    @Override
                    protected void bindViews() {
                        ButterKnife.bind(this, getView());
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
        };
    }

    @Override
    public String getSimpleName() {
        return "ReposFragment";
    }
}
