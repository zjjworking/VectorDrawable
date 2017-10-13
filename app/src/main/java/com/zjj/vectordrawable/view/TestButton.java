package com.zjj.vectordrawable.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by zjj on 17/9/15.
 */

public class TestButton extends android.support.v7.widget.AppCompatButton {
    private static String TAG = "MainActivity";
    public TestButton(Context context) {
        super(context);
    }

    public TestButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG,"TestButton +++ onTouchEvent");
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(TAG,"TestButton +++ dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }
}
