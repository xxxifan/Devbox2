package com.xxxifan.devbox.demo.ui.view.main;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.library.base.extended.ImageTranslucentActivity;
import com.xxxifan.devbox.library.util.Fragments;
import com.xxxifan.devbox.library.util.ViewUtils;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

public class MainActivity extends ImageTranslucentActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onConfigActivity() {
        super.onConfigActivity();
        transparentStatusBar();
        ViewUtils.setStatusBarDarkMode(this, true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        Fragments.add(this, new TestFragment1(), new TestFragment2())
                .into(R.id.container1, R.id.container2);
        setBackKeyListener(new BackKeyListener() {
            private int count = 0;

            @Override
            public boolean onPressed() {
                if (count < 1) {
                    count++;
                    ViewUtils.showToast("toast");
                    Observable.interval(3, TimeUnit.SECONDS)
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    count = 0;
                                }
                            });
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        transparentToolbar();
    }

    @OnClick(R.id.main_btn_1)
    public void onFirstClick(View view) {
        Fragments.checkout(this, new TestFragment1())
                .addToBackStack(true)
                .into(FRAGMENT_CONTAINER);
    }

    @OnClick(R.id.main_btn_2)
    public void onSecondClick(View view) {
        Fragments.checkout(this, new TestFragment2())
                .addToBackStack(true)
                .into(FRAGMENT_CONTAINER);
    }

    @OnClick(R.id.main_btn_3)
    public void onThirdClick(View view) {
       RecyclerActivity.start(getContext());
    }
//
//    @OnClick(R.id.btn_4)
//    public void onCrashClick(View view) {
////        startActivity(new Intent(getContext(), CrashActivity.class));
////        ViewUtils.showToast(Fragments.getLastFragment(this).toString());
//        Request request = new Request.Builder()
//                .get()
//                .url("https://api.github.com/users/xxxifan")
//                .build();
//        Http.send(request, new HttpCallback<User>() {
//            @Override
//            public void onSuccess(User result) {
//                System.out.println(result.toString());
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });
//    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.activity_main, null);
    }
}
