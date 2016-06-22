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
//    private CommonRcvAdapter mAdapter;
        private RecyclerView.Adapter mAdapter;

    @Override protected int getLayoutId() {
        return R.layout._internal_fragment_recycler;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mRecyclerView = ButterKnife.findById(view, R.id.base_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    protected abstract RecyclerView.Adapter setupAdapter();
}
