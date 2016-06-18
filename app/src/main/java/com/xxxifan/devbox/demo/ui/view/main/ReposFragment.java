package com.xxxifan.devbox.demo.ui.view.main;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.demo.data.model.Repo;
import com.xxxifan.devbox.demo.repository.GithubService;
import com.xxxifan.devbox.library.base.extended.RecyclerFragment;
import com.xxxifan.devbox.library.util.ViewUtils;
import com.xxxifan.devbox.library.util.http.Http;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        final MaterialDialog dialog = ViewUtils.getLoadingDialog(getContext());
        dialog.show();
        Http.createRetroService(GithubService.class)
                .getUserRepos(GithubService.REPO_TYPE_OWNER, GithubService.REPO_SORT_UPDATED, GithubService.DIRECTION_DESC)
                .enqueue(new Callback<List<Repo>>() {
                    @Override
                    public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                        mRepoList = new ArrayList<>(response.body());
                        getAdapter().notifyDataSetChanged();
                        ViewUtils.showToast("data received");
                        ViewUtils.dismissDialog(dialog);
                    }

                    @Override
                    public void onFailure(Call<List<Repo>> call, Throwable t) {
                        t.printStackTrace();
                        ViewUtils.dismissDialog(dialog);
                    }
                });

    }

    @Override
    protected RecyclerView.Adapter setAdapter() {
//        return new CommonRcvAdapter<Repo>(null) {
//            @NonNull
//            @Override
//            public AdapterItem<Repo> createItem(Object type) {
//                return new BaseAdapterItem<Repo>() {
//                    @BindView(R.id.repo_name)
//                    TextView titleText;
//                    @BindView(R.id.repo_rank)
//                    TextView rankText;
//
//                    @Override
//                    protected void bindViews() {
////                        ButterKnife.bind(this, getView());
//                        titleText = ButterKnife.findById(getView(), R.id.repo_name);
//                        rankText = ButterKnife.findById(getView(), R.id.repo_rank);
//                    }
//
//                    @Override
//                    public int getLayoutResId() {
//                        return R.layout.item_repos;
//                    }
//
//                    @Override
//                    public void handleData(Repo repo, int index) {
//                        titleText.setText(repo.name);
//                        String rankStr = String.format("Fork:%s Star:%s", repo.forks, repo.stargazers_count);
//                        rankText.setText(rankStr);
//                    }
//
//                };
//            }
//        };
        return new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RepoViewHolder(View.inflate(getContext(), R.layout.item_repos, null));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                RepoViewHolder repoViewHolder = (RepoViewHolder) holder;
                Repo repo = mRepoList.get(position);
                repoViewHolder.titleText.setText(repo.name);
                String rankStr = String.format("Fork:%s Star:%s", repo.forks, repo.stargazers_count);
                repoViewHolder.rankText.setText(rankStr);
            }

            @Override
            public int getItemCount() {
                return mRepoList == null ? 0 : mRepoList.size();
            }
        };
    }

    @Override
    public String getSimpleName() {
        return "ReposFragment";
    }

    public static class RepoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.repo_name)
        TextView titleText;
        @BindView(R.id.repo_rank)
        TextView rankText;

        public RepoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
