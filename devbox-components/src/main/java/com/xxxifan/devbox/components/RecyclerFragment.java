/*
 * Copyright(c) 2016 xxxifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxxifan.devbox.components;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xxxifan.devbox.core.base.BaseFragment;
import com.xxxifan.devbox.core.base.DataLoader;
import com.xxxifan.devbox.core.util.Tests;

import kale.adapter.CommonRcvAdapter;
import kale.adapter.RcvAdapterWrapper;
import kale.adapter.item.AdapterItem;

/**
 * Created by xifan on 6/14/16.
 */
public abstract class RecyclerFragment<T> extends BaseFragment {
    public static final int BASE_RECYCLER_ID = R.id.base_recycler_view;

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
            mRecyclerView = $(view, BASE_RECYCLER_ID);
            Tests.checkNull(mRecyclerView);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerWrapper = new RcvAdapterWrapper(createAdapter(), layoutManager);
            mRecyclerView.setLayoutManager(mRecyclerWrapper.getLayoutManager());
            mRecyclerView.setAdapter(mRecyclerWrapper);
        }
        return view;
    }

    /**
     * @return create adapter, default use CommonRcvAdapter.
     */
    protected RecyclerView.Adapter createAdapter() {
        return new CommonRcvAdapter<T>(null) {
            @NonNull @Override public AdapterItem<T> createItem(Object type) {
                return onCreateAdapterItem(type);
            }

            @Override public Object getItemType(T t) {
                return getAdapterItemType(t);
            }
        };
    }

    protected Object getAdapterItemType(T t) {
        return -1;
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
        if (mRecyclerView != null && mRecyclerView.getParent() instanceof FrameLayout) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) mRecyclerView.getParent()).addView(view, params);
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
                        getDataLoader().forceLoad();
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

    /**
     * Default implements for {@link DataLoader.ListLoadCallback#notifyDataLoaded()},
     * better call this through it.
     */
    public void notifyDataLoaded() {
        if (getAdapterWrapper() != null) {
            getAdapterWrapper().notifyDataSetChanged();
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
     * @return helper to retrieve CommonRcvAdapter, if not a CommonRcvAdapter, return null.
     */
    @SuppressWarnings("unchecked")
    protected CommonRcvAdapter<T> getCommonRcvAdapter() {
        if (mRecyclerWrapper.getWrappedAdapter() instanceof CommonRcvAdapter) {
            return (CommonRcvAdapter<T>) getAdapter();
        } else {
            return null;
        }
    }

    /**
     * @return get RecyclerWrapper
     */
    protected RcvAdapterWrapper getAdapterWrapper() {
        return mRecyclerWrapper;
    }

    protected abstract AdapterItem<T> onCreateAdapterItem(Object type);
}