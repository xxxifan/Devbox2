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

package com.xxxifan.devbox.library.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import rx.Observable;
import rx.functions.Action0;

/**
 * Created by xifan on 4/6/16.
 */
public class ViewUtils {

    private static final String CONFIG_SHOW_NAVBAR = "config_showNavigationBar";
    private static final String CONFIG_FORCE_NAVBAR = "dev_force_show_navbar";
    private static final String CONFIG_TOOLBAR_HEIGHT = "status_bar_height";

    private static boolean sHasTranslucentNavBar;
    private static float sDensity = 0;
    private static float sScaledDensity = 0;
    private static int sStatusBarHeight;
    private static int sNavBarHeight;
    private static int sScreenHeight;
    private static int sScreenWidth;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // find custom settings first: force hardware key by build.prop or cm enabler
            IOUtils.runCmd(new String[]{"getprop", "qemu.hw.mainkeys"}, new IOUtils.CommandCallback() {
                public void done(String forceKey, IOException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }

                    if (forceKey.equals("0")) {
                        sHasTranslucentNavBar = true;
                        return;
                    }

                    Context context = Devbox.getAppDelegate();
                    // check cm settings
                    ContentResolver resolver = context.getContentResolver();
                    boolean forceCm = Settings.Secure.getInt(resolver, CONFIG_FORCE_NAVBAR, 0) == 1;

                    // fallback, use common method.
                    sHasTranslucentNavBar = forceCm || readInternalBoolean(CONFIG_SHOW_NAVBAR, context
                            .getResources(), !ViewConfiguration.get(context).hasPermanentMenuKey());
                }
            });
        }
    }

    protected ViewUtils() {
    }

    public static int dp2px(float dp) {
        return (int) (dp * getDensity() + 0.5f);
    }

    public static float sp2px(float sp) {
        getDensity();
        return sp * sScaledDensity;
    }

    public static float px2dp(int px) {
        return px / getDensity() + 0.5f;
    }

    public static boolean hasTranslucentNavBar() {
        return sHasTranslucentNavBar;
    }

    public static boolean hasTranslucentBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static int getSystemBarHeight() {
        if (sStatusBarHeight == 0) {
            sStatusBarHeight = readInternalDimen(CONFIG_TOOLBAR_HEIGHT, Devbox
                    .getAppDelegate()
                    .getResources(), dp2px(24));
        }
        return sStatusBarHeight;
    }

    public static int getDeviceScreenHeight() {
        if (sScreenHeight == 0) {
            sScreenHeight = getDisplayMetrics().heightPixels;
        }
        return sScreenHeight;
    }

    public static int getDeviceScreenWidth() {
        if (sScreenWidth == 0) {
            sScreenWidth = getDisplayMetrics().widthPixels;
        }
        return sScreenWidth;
    }

    public static int getNavBarHeight() {
        if (sNavBarHeight == 0) {
            int deviceScreenHeight = getDeviceScreenHeight();
            int displayHeight = Devbox.getAppDelegate().getResources()
                    .getDisplayMetrics().heightPixels;
            sNavBarHeight = deviceScreenHeight - displayHeight;
            if (sNavBarHeight <= 0) {
                sNavBarHeight = dp2px(48);
            }
        }
        return sNavBarHeight;
    }

    public static int getWindowHeight() {
        return getDeviceScreenHeight() - getSystemBarHeight() - getNavBarHeight();
    }

    public static float getDensity() {
        if (sDensity == 0) {
            try {
                DisplayMetrics dm = Devbox.getAppDelegate().getResources().getDisplayMetrics();
                sDensity = dm.density;
                sScaledDensity = dm.scaledDensity;
            } catch (Exception e) {
                sDensity = sScaledDensity = 2f;
            }
        }
        return sDensity;
    }

    /**
     * set status bar icon to light theme, which is called dark mode.
     * should be called in onCreate()
     */
    public static void setStatusBarDarkMode(Activity activity, boolean darkmode) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }

        Window window = activity.getWindow();
        boolean changed = false;
        // try miui
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = window.getClass()
                    .getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            changed = true;
        } catch (Exception ignored) {
        }

        // try flyme
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkmode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            changed = true;
        } catch (Exception ignored) {
        }

        if (!changed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int visibility = window.getDecorView().getSystemUiVisibility();
            if (darkmode) {
                visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(visibility);
        }
    }

    public static void closeKeyboard(Context context) {
        if (context != null) {
            View focus = ((Activity) context).getCurrentFocus();
            if (focus != null && focus instanceof EditText) {
                closeKeyboard((EditText) focus);
            }
        }
    }

    /**
     * @param editor one of EditText
     */
    public static void closeKeyboard(EditText editor) {
        ((InputMethodManager) editor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editor.getWindowToken(), 0);
    }

    public static void showKeyboard(EditText editor) {
        ((InputMethodManager) editor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        editor.requestFocus();
    }

    public static void addTextDelLine(TextView textView) {
        textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Simple dialog builder with default buttons.
     */
    public static MaterialDialog.Builder getSimpleDialogBuilder(Context context, String text) {
        return new MaterialDialog.Builder(context)
                .content(text)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    public static MaterialDialog.Builder getSimpleDialogBuilder(Context context) {
        return new MaterialDialog.Builder(context)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    public static MaterialDialog getLoadingDialog(Context context) {
        return getLoadingDialog(context, context.getString(R.string.msg_loading));
    }

    public static MaterialDialog getLoadingDialog(Context context, String loadingText) {
        return new MaterialDialog.Builder(context)
                .progress(true, 0)
                .content(loadingText)
                .cancelable(false)
                .build();
    }

    public static void showToast(@StringRes int resId, int duration) {
        Toast.makeText(Devbox.getAppDelegate(), resId, duration).show();
    }

    public static void showToast(String toastStr, int duration) {
        Toast.makeText(Devbox.getAppDelegate(), toastStr, duration).show();
    }

    public static void showToast(@StringRes int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showToast(String toastStr) {
        showToast(toastStr, Toast.LENGTH_SHORT);
    }

    public static int getCompatColor(@ColorRes int color) {
        return ContextCompat.getColor(Devbox.getAppDelegate(), color);
    }

    public static Drawable getCompatDrawable(@DrawableRes int res) {
        return ContextCompat.getDrawable(Devbox.getAppDelegate(), res);
    }

    /**
     * transformer for Observables needs a loading dialog.
     */
    public static <T> Observable.Transformer<T, T> loadingObservable(final Context context) {
        return new Observable.Transformer<T, T>() {
            private MaterialDialog dialog;

            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        .doOnSubscribe(new Action0() {
                            @Override public void call() {
                                dialog = getLoadingDialog(context);
                                dialog.show();
                            }
                        })
                        .doOnTerminate(new Action0() {
                            @Override public void call() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                    dialog = null;
                                }
                            }
                        });
            }
        };
    }

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    // ############ Private #############

    private static int readInternalDimen(String key, Resources res, int fallback) {
        int resourceId = res.getIdentifier(key, "dimen", "android");
        return resourceId > 0 ? res.getDimensionPixelSize(resourceId) : fallback;
    }

    private static boolean readInternalBoolean(String key, Resources res, boolean fallback) {
        int resourceId = res.getIdentifier(key, "bool", "android");
        return resourceId != 0 ? res.getBoolean(resourceId) : fallback;
    }

    private static DisplayMetrics getDisplayMetrics() {
        Display display = ((WindowManager) Devbox.getAppDelegate()
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((WindowManager) Devbox.getAppDelegate().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getRealMetrics(metrics);
        } else {
            try {
                Method method = display.getClass()
                        .getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, metrics);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return metrics;
    }
}
