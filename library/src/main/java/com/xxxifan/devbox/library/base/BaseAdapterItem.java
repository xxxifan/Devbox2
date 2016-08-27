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

package com.xxxifan.devbox.library.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import kale.adapter.item.AdapterItem;

/**
 * Created by xifan on 4/10/16.
 * BaseAdapterItem for CommonAdapter.
 */
public abstract class BaseAdapterItem<T> implements AdapterItem<T> {

    private View root;
    private T data;
    private int position;

    private ItemClickListener<T> mItemClickListener;

    public BaseAdapterItem() {
    }

    @Override
    public void bindViews(View root) {
        this.root = root;
        if (mItemClickListener != null && !root.hasOnClickListeners()) {
            root.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getData(), getPosition());
                    }
                }
            });
        }
        bindViews();
    }

    @Override public void setViews() {}

    @CallSuper @Override public void handleData(T data, int position) {
        this.data = data;
        this.position = position;
    }

    public View getView() {
        return root;
    }

    public T getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }

    public void setOnItemClickListener(@NonNull ItemClickListener<T> listener) {
        mItemClickListener = listener;

        if (getView() != null) {
            getView().setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getData(), getPosition());
                    }
                }
            });
        }
    }

    protected abstract void bindViews();

    public interface ItemClickListener<T> {
        void onItemClick(View v, T data, int index);
    }

}
