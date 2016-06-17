package com.xxxifan.devbox.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.xxxifan.devbox.library.base.BaseAdapterItem;
import com.xxxifan.devbox.library.base.extended.RecyclerFragment;

import java.util.ArrayList;
import java.util.List;

import kale.adapter.CommonRcvAdapter;
import kale.adapter.item.AdapterItem;

/**
 * Created by xifan on 6/17/16.
 */
public class RecyclerTestFragment extends RecyclerFragment {

    @Override protected void onSetupFragment(View view, Bundle savedInstanceState) {

    }

    @Override protected CommonRcvAdapter setAdapter() {
        List<User> list = new ArrayList<>();
        return new CommonRcvAdapter<User>(list) {
            @NonNull @Override public AdapterItem<User> createItem(Object type) {
                return new BaseAdapterItem<User>() {
                    @Override protected void bindViews() {

                    }

                    @Override public int getLayoutResId() {
                        return 0;
                    }

                    @Override public void handleData(User user, int i) {

                    }

                };
            }
        };
    }

    @Override public String getSimpleName() {
        return null;
    }
}
