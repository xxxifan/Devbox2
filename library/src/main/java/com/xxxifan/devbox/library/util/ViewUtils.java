package com.xxxifan.devbox.library.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xxxifan.devbox.library.base.Devbox;

/**
 * Created by xifan on 4/6/16.
 */
public class ViewUtils {
    private static float sDensity = 0;
    private static float sScaledDensity = 0;

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

    public static MaterialDialog.Builder getSimpleDialogBuilder(Context context) {
        return new MaterialDialog.Builder(context)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
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
}
