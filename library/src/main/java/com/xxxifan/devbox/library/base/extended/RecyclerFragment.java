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
import kale.adapter.RcvAdapterWrapper;

/**
 * Created by xifan on 6/14/16.
 */
public abstract class RecyclerFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RcvAdapterWrapper mRecyclerWrapper;

    @Override protected int getLayoutId() {
        return R.layout._internal_fragment_recycler;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mRecyclerView = ButterKnife.findById(view, R.id.base_recycler_view);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerWrapper = new RcvAdapterWrapper(setupAdapter(), layoutManager);
            mRecyclerView.setLayoutManager(mRecyclerWrapper.getLayoutManager());
            mRecyclerView.setAdapter(mRecyclerWrapper);
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
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerWrapper.getLayoutManager();
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
        if (mRecyclerWrapper != null) {
            mRecyclerWrapper.setLayoutManager(layoutManager);
        }
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected RecyclerView.Adapter getAdapter() {
        return mRecyclerWrapper.getWrappedAdapter();
    }

    /**
     * @return get RecyclerWrapper
     */
    protected RecyclerView.Adapter getAdapterWrapper() {
        return mRecyclerWrapper;
    }

    protected void notifyDataLoaded() {
        if (getAdapterWrapper() != null) {
            getAdapterWrapper().notifyDataSetChanged();
        }
        if (getDataLoader() != null) {
            getDataLoader().notifyPageLoaded();
        }
    }

    protected abstract RecyclerView.Adapter setupAdapter();
}
