package com.sandy.seoul_matcheap.util.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-23
 * @desc
 */
public class SoftKeyboardDetectorView extends View {
    private boolean mShownKeyboard;
    private OnShownKeyboardListener mOnShownSoftKeyboard;
    private OnHiddenKeyboardListener onHiddenSoftKeyboard;

    private Activity activity;

    public SoftKeyboardDetectorView(Context context) {
        this(context, null);
        this.activity = (Activity) context;
    }

    public SoftKeyboardDetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activity = (Activity) context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        int diffHeight = (screenHeight - statusBarHeight) - h;
        if (diffHeight > 100 && !mShownKeyboard) { // 모든 키보드는 100px보다 크다고 가정
            mShownKeyboard = true;
            onShownSoftKeyboard();
        } else if (diffHeight < 100 && mShownKeyboard) {
            mShownKeyboard = false;
            onHiddenSoftKeyboard();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void onHiddenSoftKeyboard() {
        if (onHiddenSoftKeyboard != null)
            onHiddenSoftKeyboard.onHiddenSoftKeyboard();
    }

    public void onShownSoftKeyboard() {
        if (mOnShownSoftKeyboard != null)
            mOnShownSoftKeyboard.onShowSoftKeyboard();
    }

    public void setOnShownKeyboard(OnShownKeyboardListener listener) {
        mOnShownSoftKeyboard = listener;
    }

    public void setOnHiddenKeyboard(OnHiddenKeyboardListener listener) {
        onHiddenSoftKeyboard = listener;
    }

    public interface OnShownKeyboardListener {
        void onShowSoftKeyboard();
    }

    public interface OnHiddenKeyboardListener {
        void onHiddenSoftKeyboard();
    }
}
