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
import android.view.View;

import kale.adapter.item.AdapterItem;

/**
 * Created by xifan on 4/10/16.
 * Devbox BaseAdapterItem for CommonAdapter.
 */
public abstract class BaseAdapterItem<T> implements AdapterItem<T> {

    private View root;
    private T data;
    private int position;

    private ItemClickListener<T> mItemClickListener;
    private ItemLongClickListener<T> mItemLongClickListener;

    public BaseAdapterItem() {
    }

    @Override
    public void bindViews(View root) {
        this.root = root;

        // bindViews may happens after set listeners
        root.post(new Runnable() {
            @Override public void run() {
                setOnItemClickListener(mItemClickListener);
                setOnItemLongClickListener(mItemLongClickListener);
            }
        });
    }

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

    public void setOnItemClickListener(ItemClickListener<T> listener) {
        mItemClickListener = listener;

        if (getView() != null) {
            getView().setOnClickListener(listener == null ? null : new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getData(), getPosition());
                    }
                }
            });
        }
    }

    public void setOnItemLongClickListener(ItemLongClickListener<T> listener) {
        mItemLongClickListener = listener;

        if (getView() != null) {
            getView().setOnLongClickListener(listener == null ? null : new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    if (mItemLongClickListener != null) {
                        mItemLongClickListener.onItemLongClick(v, getData(), getPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public interface ItemClickListener<T> {
        void onItemClick(View v, T data, int index);
    }

    public interface ItemLongClickListener<T> {
        void onItemLongClick(View v, T data, int index);
    }

}
