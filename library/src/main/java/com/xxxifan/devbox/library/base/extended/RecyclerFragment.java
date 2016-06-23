package com.xxxifan.devbox.library.base.extended;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by xifan on 6/14/16.
 */
public abstract class RecyclerFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override protected int getLayoutId() {
        return R.layout._internal_fragment_recycler;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mRecyclerView = ButterKnife.findById(view, R.id.base_recycler_view);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = setupAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    protected void setItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (mRecyclerView != null) {
            mRecyclerView.addItemDecoration(decoration);
        }
    }

    /**
     * @param loadThreshold indicate start load while list have those left
     */
    protected void enableScrollToLoad(final int loadThreshold) {
        if (mRecyclerView != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mLayoutManager;
                    final int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    final int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - loadThreshold && dy > 0) {
                        getDataLoader().startRefresh();
                    }
                }
            });
        }
    }

    protected void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    protected void notifyDataLoaded() {
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
        }
        if (getDataLoader() != null) {
            getDataLoader().notifyPageLoaded();
        }
    }

    protected abstract RecyclerView.Adapter setupAdapter();
}
