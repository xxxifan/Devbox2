package com.xxxifan.devbox.base;

import android.view.View;

import kale.adapter.item.AdapterItem;

/**
 * Created by xifan on 4/10/16.
 * BaseAdapterItem for CommonAdapter.
 */
public abstract class BaseAdapterItem<T> implements AdapterItem<T> {

    private View root;

    public BaseAdapterItem() {
    }

    @Override
    public void bindViews(View root) {
        this.root = root;
        bindViews();
    }

    public View getView() {
        return root;
    }

    protected abstract void bindViews();

}
