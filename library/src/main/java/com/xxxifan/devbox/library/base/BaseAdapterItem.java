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

    public BaseAdapterItem() {
    }

    @Override
    public void bindViews(View root) {
        this.root = root;
        bindViews();
    }

    @Override public void setViews() {}

    @Override public void handleData(T t, int i) {
        this.data = t;
        this.position = i;
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

    protected abstract void bindViews();

}
