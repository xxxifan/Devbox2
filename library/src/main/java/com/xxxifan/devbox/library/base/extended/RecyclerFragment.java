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

import kale.adapter.RcvAdapterWrapper;

/**
 * Created by xifan on 6/14/16.
 */
public abstract class RecyclerFragment extends BaseFragment {
    protected static final int PTR_LAYOUT_ID = R.id.recycler_ptr_layout;

    private RecyclerView mRecyclerView;
    private RcvAdapterWrapper mRecyclerWrapper;
    private View mEmptyView;

    @Override protected int getLayoutId() {
        return R.layout._internal_fragment_recycler;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mRecyclerView = $(view, R.id.base_recycler_view);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerWrapper = new RcvAdapterWrapper(setupAdapter(), layoutManager);
            mRecyclerView.setLayoutManager(mRecyclerWrapper.getLayoutManager());
            mRecyclerView.setAdapter(mRecyclerWrapper);
        }
        return view;
    }

    public void setFooterView(View view) {
        if (mRecyclerWrapper != null) {
            mRecyclerWrapper.setFooterView(view);
        }
    }

    public void setHeaderView(View view) {
        if (mRecyclerWrapper != null) {
            mRecyclerWrapper.setHeaderView(view);
        }
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
        mEmptyView.setVisibility(View.GONE);
        if (getView() != null) {
            ((ViewGroup) getView()).addView(view);
        }
    }

    public void setItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (mRecyclerView != null) {
            mRecyclerView.addItemDecoration(decoration);
        }
    }

    /**
     * @param loadThreshold indicate start load while list have those left
     */
    public void enableScrollToLoad(final int loadThreshold) {
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

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecyclerWrapper != null) {
            mRecyclerWrapper.setLayoutManager(layoutManager);
        }
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    public void notifyDataLoaded() {
        if (getAdapterWrapper() != null) {
            getAdapterWrapper().notifyDataSetChanged();
        }
        if (getDataLoader() != null) {
            getDataLoader().notifyPageLoaded();
        }

        if (mEmptyView != null) {
            if (getAdapter().getItemCount() == 0) {
                getRecyclerView().setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                getRecyclerView().setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
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

    protected abstract RecyclerView.Adapter setupAdapter();
}
